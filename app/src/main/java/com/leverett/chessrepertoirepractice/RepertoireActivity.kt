package com.leverett.chessrepertoirepractice

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.leverett.chessrepertoirepractice.ui.views.LoadConfigurationAdapter
import com.leverett.chessrepertoirepractice.ui.views.PlaySettingButton
import com.leverett.chessrepertoirepractice.ui.views.RepertoireListAdapter
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.pgn.makeLineTreeText
import com.leverett.repertoire.chess.pgn.parseAnnotatedPgnToBook
import com.leverett.repertoire.chess.settings.PlaySettings

class RepertoireActivity : AppCompatActivity() {

    private val repertoireManager = RepertoireManager
    private lateinit var repertoireView: ExpandableListView
    private val repertoireViewAdapter = RepertoireListAdapter(true)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repertoire)
        repertoireView = findViewById(R.id.repertoire_list_view)
        repertoireView.setAdapter(repertoireViewAdapter)

        playerBestOptionView = findViewById(R.id.player_best)
        playerTheoryOptionView = findViewById(R.id.player_theory)
        playerGambitsOptionView = findViewById(R.id.player_gambits)
        playerPreferredOptionView = findViewById(R.id.player_preferred)
        opponentBestOptionView = findViewById(R.id.opponent_best)
        opponentTheoryOptionView = findViewById(R.id.opponent_theory)
        opponentGambitsOptionView = findViewById(R.id.opponent_gambit)
        opponentMistakesOptionView = findViewById(R.id.opponent_mistakes)

        for (view in playOptionViews) {
            view.setOnClickListener { view -> togglePlayOption(view as PlaySettingButton) }
        }
        refreshPlayOptionButtonColors()
    }

    fun loadPgnButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.load_pgn_popup, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(repertoireView, Gravity.CENTER, 0, -100)
        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            val pgn = popupView.findViewById<TextInputEditText>(R.id.pgn_input).text.toString()
            val book = parseAnnotatedPgnToBook(pgn)
            repertoireManager.addToRepertoire(book)
            repertoireViewAdapter.notifyDataSetChanged()
            popupWindow.dismiss()
        }
    }

    fun exportPgnButton(view: View) {
        val repertoireText = repertoireManager.repertoire.lineTrees.joinToString("\n\n\n") { makeLineTreeText(it) }
        val clipData = ClipData.newPlainText("label", repertoireText)
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(clipData)
        val toast = Toast.makeText(applicationContext, R.string.clipboard_toast, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun saveConfigurationButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.save_configuration_popup, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(repertoireView, Gravity.CENTER, 0, 0)
        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            val configName = popupView.findViewById<TextInputEditText>(R.id.configuration_name_input).text.toString()
            repertoireManager.saveConfiguration(configName)
            popupWindow.dismiss()
        }
    }

    fun loadConfigurationButton(view: View) {
        if (repertoireManager.configurations.isNotEmpty()) {
            val popupView = layoutInflater.inflate(R.layout.load_configuration_popup, null) as ConstraintLayout
            val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
            popupWindow.showAtLocation(repertoireView, Gravity.CENTER, 0, 0)
            popupView.findViewById<RecyclerView>(R.id.configuration_options).adapter = LoadConfigurationAdapter ({popupWindow.dismiss()}, {repertoireViewAdapter.refreshListViewChecks()})
            popupView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                popupWindow.dismiss()
            }
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