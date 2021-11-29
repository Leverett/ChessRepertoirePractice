package com.leverett.chessrepertoirepractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.leverett.chessrepertoirepractice.ui.views.PlaySettingButton
import com.leverett.chessrepertoirepractice.ui.views.RepertoireListAdapter
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.settings.PlaySettings
import android.widget.*
import com.leverett.chessrepertoirepractice.utils.*
import com.leverett.repertoire.chess.lines.LineTree


class RepertoireActivity : AppCompatActivity() {

    private val repertoireManager = RepertoireManager
    private lateinit var repertoireView: ExpandableListView
    private lateinit var repertoireViewAdapter: RepertoireListAdapter
    private lateinit var configurationsView: Spinner
    private lateinit var configurationViewAdapter: ArrayAdapter<String>

    private val playSettings: PlaySettings
        get() = repertoireManager.playSettings

    private lateinit var playerBestOptionView: PlaySettingButton
    private lateinit var playerTheoryOptionView: PlaySettingButton
    private lateinit var playerGambitsOptionView: PlaySettingButton
    private lateinit var playerPreferredOptionView: PlaySettingButton
    private lateinit var opponentBestOptionView: PlaySettingButton
    private lateinit var opponentTheoryOptionView: PlaySettingButton
    private lateinit var opponentGambitsOptionView: PlaySettingButton
    private lateinit var opponentMistakesOptionView: PlaySettingButton
    private val playOptionViews: List<PlaySettingButton>
        get() {
            return listOf(
                playerBestOptionView,
                playerTheoryOptionView,
                playerGambitsOptionView,
                playerPreferredOptionView,
                opponentBestOptionView,
                opponentTheoryOptionView,
                opponentGambitsOptionView,
                opponentMistakesOptionView
            )
        }

    private lateinit var selectAllView: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repertoire)

        setupConfigurationsMenu()
        setupSelectAllView()
        setupRepertoireView()
        setupPlayOptionsButtons()
    }

    private fun setupRepertoireView() {
        repertoireView = findViewById(R.id.repertoire_list_view)
        repertoireViewAdapter = RepertoireListAdapter(applicationContext, layoutInflater, repertoireView, selectAllView)
        repertoireView.setAdapter(repertoireViewAdapter)
    }

    private fun setupConfigurationsMenu() {
        configurationViewAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, repertoireManager.configurationNames)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        configurationsView = findViewById(R.id.configurations_menu)
        configurationsView.adapter = configurationViewAdapter
        if (repertoireManager.currentConfiguration != null) {
            configurationsView.setSelection(configurationViewAdapter.getPosition(repertoireManager.currentConfiguration))
        }
        configurationsView.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                repertoireManager.loadConfiguration(configurationViewAdapter.getItem(position)!!)
                repertoireViewAdapter.notifyDataSetChanged()
                refreshPlayOptionButtonColors()
            }
        }
    }

    private fun setupPlayOptionsButtons() {
        playerBestOptionView = findViewById(R.id.player_best)
        playerTheoryOptionView = findViewById(R.id.player_theory)
        playerGambitsOptionView = findViewById(R.id.player_gambits)
        playerPreferredOptionView = findViewById(R.id.player_preferred)
        opponentBestOptionView = findViewById(R.id.opponent_best)
        opponentTheoryOptionView = findViewById(R.id.opponent_theory)
        opponentGambitsOptionView = findViewById(R.id.opponent_gambit)
        opponentMistakesOptionView = findViewById(R.id.opponent_mistakes)

        for (view in playOptionViews) {
            view.setOnClickListener { togglePlayOption(it as PlaySettingButton) }
        }
        refreshPlayOptionButtonColors()
    }

    private fun setupSelectAllView() {
        selectAllView = findViewById(R.id.select_all_option)
        selectAllView.isChecked = repertoireManager.isFullRepertoire()
        selectAllView.setOnClickListener {
            repertoireManager.setFullActiveRepertoire(selectAllView.isChecked)
            storeConfigurations(applicationContext)
            repertoireViewAdapter.notifyDataSetChanged()
        }
    }

    fun newConfigurationButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.new_configuration_popup, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, popupWidthDp(applicationContext, 2.5f), ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(repertoireView, Gravity.CENTER, 0, -100)
        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            val configurationName = popupView.findViewById<TextInputEditText>(R.id.configuration_name_input).text.toString()
            repertoireManager.newConfiguration(configurationName)
            storeConfigurations(applicationContext)
            updateConfigurationsMenu()
            popupWindow.dismiss()
        }
        popupView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            popupWindow.dismiss()
        }
    }

    fun deleteConfigurationButton(view: View) {
        if (repertoireManager.currentConfiguration != null) {
            makeConfirmationDialog(applicationContext, layoutInflater, repertoireView, "Delete Configuration: ${repertoireManager.currentConfiguration}?")
            {
                repertoireManager.deleteConfiguration()
                storeConfigurations(applicationContext)
                updateConfigurationsMenu()
            }
        }
    }

    fun syncRepertoireButton(view: View) {
        if (accountInfo.incompleteInfo) {
            makeAccountInfoPopup(applicationContext, layoutInflater, repertoireView) { syncRepertoire() }
        } else {
            syncRepertoire()
        }
    }

    private fun syncRepertoire() {
        val expandedLineTrees: Set<String> = getExpandedLineTrees()
        Thread{
            syncRepertoire(applicationContext)
            runOnUiThread{
                repertoireViewAdapter.notifyDataSetChanged()
                for (position in 0 until repertoireViewAdapter.groupCount) {
                    if (expandedLineTrees.contains((repertoireViewAdapter.getGroup(position) as LineTree).name)) {
                        repertoireView.expandGroup(position)
                    } else {
                        repertoireView.collapseGroup(position)
                    }
                }
            }
        }.start()
    }

    private fun getExpandedLineTrees(): Set<String> {
        val result = mutableSetOf<String>()
        for (position in 0 until repertoireViewAdapter.groupCount) {
            if (repertoireView.isGroupExpanded(position)) {
                result.add((repertoireViewAdapter.getGroup(position) as LineTree).name)
            }
        }
        return result
    }

    fun startTrainingButton(view: View) {
        val intent = Intent(this, PracticeActivity::class.java)
        startActivity(intent)
    }

    private fun updateConfigurationsMenu() {
        configurationViewAdapter.clear()
        configurationViewAdapter.addAll(repertoireManager.configurationNames)
        configurationViewAdapter.notifyDataSetChanged()
        if (repertoireManager.currentConfiguration != null) {
            configurationsView.setSelection(configurationViewAdapter.getPosition(repertoireManager.currentConfiguration))
        }
    }

    private fun togglePlayOption(view: PlaySettingButton) {
        when (view) {
            playerBestOptionView -> playSettings.playerBest = !playSettings.playerBest
            playerTheoryOptionView -> playSettings.playerTheory = !playSettings.playerTheory
            playerGambitsOptionView -> playSettings.playerGambits = !playSettings.playerGambits
            playerPreferredOptionView -> playSettings.playerPreferred = !playSettings.playerPreferred
            opponentBestOptionView -> playSettings.opponentBest = !playSettings.opponentBest
            opponentTheoryOptionView -> playSettings.opponentTheory = !playSettings.opponentTheory
            opponentGambitsOptionView -> playSettings.opponentGambits = !playSettings.opponentGambits
            opponentMistakesOptionView -> playSettings.opponentMistakes = !playSettings.opponentMistakes
        }
        storeConfigurations(applicationContext)
        refreshPlayOptionButtonColors()
    }

    private fun refreshPlayOptionButtonColors() {
        for (view in playOptionViews) {
            when (view) {
                playerBestOptionView -> playerBestOptionView.active = playSettings.playerBest
                playerTheoryOptionView -> playerTheoryOptionView.active = playSettings.playerTheory
                playerGambitsOptionView -> playerGambitsOptionView.active = playSettings.playerGambits
                playerPreferredOptionView -> playerPreferredOptionView.active = playSettings.playerPreferred
                opponentBestOptionView -> opponentBestOptionView.active = playSettings.opponentBest
                opponentTheoryOptionView -> opponentTheoryOptionView.active = playSettings.opponentTheory
                opponentGambitsOptionView -> opponentGambitsOptionView.active = playSettings.opponentGambits
                opponentMistakesOptionView -> opponentMistakesOptionView.active = playSettings.opponentMistakes
            }
            view.updateColor()
        }
    }

}