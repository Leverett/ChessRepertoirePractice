package com.leverett.chessrepertoirepractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.leverett.chessrepertoirepractice.ui.views.RepertoireListAdapter
import com.leverett.repertoire.chess.RepertoireManager
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.leverett.chessrepertoirepractice.ui.views.ConfigurationListAdapter
import com.leverett.chessrepertoirepractice.utils.*
import com.leverett.repertoire.chess.RepertoireManager.DEFAULT_CONFIGURATION_NAME
import com.leverett.repertoire.chess.lines.LineTree
import com.leverett.repertoire.chess.settings.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RepertoireActivity : AppCompatActivity() {

    private val repertoireManager = RepertoireManager
    private val signInResultLauncher = registerSignInResult(this)
    private val driveInfo = DriveInfo
    private lateinit var repertoireView: ExpandableListView
    private lateinit var repertoireViewAdapter: RepertoireListAdapter
    private lateinit var configurationsView: Spinner
    private lateinit var configurationViewAdapter: ConfigurationListAdapter

    private lateinit var selectAllView: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repertoire)

        setupConfigurationsMenu()
        setupSelectAllView()
        setupRepertoireView()
    }

    private fun setupRepertoireView() {
        repertoireView = findViewById(R.id.repertoire_list_view)
        repertoireViewAdapter = RepertoireListAdapter(applicationContext, layoutInflater, repertoireView, selectAllView)
        repertoireView.setAdapter(repertoireViewAdapter)
    }

    private fun setupConfigurationsMenu() {
        configurationViewAdapter = ConfigurationListAdapter(this)
        configurationsView = findViewById(R.id.configurations_menu)
        configurationsView.adapter = configurationViewAdapter
        configurationsView.setSelection(configurationViewAdapter.getPosition(repertoireManager.configuration))
        configurationsView.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                repertoireManager.loadConfiguration((configurationViewAdapter.getItem(position) as Configuration).name)
                repertoireViewAdapter.notifyDataSetChanged()
            }
        }
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

    fun syncOptions(view: View) {
        val popupView = layoutInflater.inflate(R.layout.sync_options_popup, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, popupWidthDp(applicationContext, 2.5f), ConstraintLayout.LayoutParams.WRAP_CONTENT, true)

        popupView.findViewById<Button>(R.id.lichess_sync_button).setOnClickListener{
            syncRepertoireWithLichess(popupWindow)
        }
        popupView.findViewById<Button>(R.id.download_configurations_button).setOnClickListener{
            makeConfirmationDialog(applicationContext, layoutInflater, repertoireView, "Download Configurations?") {
                CoroutineScope(Dispatchers.Main).launch { downloadConfigurations() }
                popupWindow.dismiss()
            }
        }
        popupView.findViewById<Button>(R.id.upload_repertoire_button).setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch { uploadConfigurations() }
            popupWindow.dismiss()
        }
        popupWindow.showAtLocation(repertoireView, Gravity.CENTER, 0, 0)
    }

    private fun syncRepertoireWithLichess(popupWindow: PopupWindow) {
        if (accountInfo.incompleteInfo) {
            makeAccountInfoPopup(applicationContext, layoutInflater, repertoireView) { syncRepertoire() }
        } else {
            syncRepertoire()
        }
        popupWindow.dismiss()
    }

    private suspend fun downloadConfigurations() {
        if (driveInfo.incomplete) {
            signIn(this, signInResultLauncher)
        }
        downloadConfigurations(applicationContext)
        loadConfigurations(applicationContext)
        updateConfigurationsMenu()
    }

    private fun uploadConfigurations() {
        if (driveInfo.incomplete) {
            signIn(this, signInResultLauncher)
        }
        CoroutineScope(Dispatchers.IO).launch { uploadRepertoire(applicationContext) }
    }

    fun newConfigurationButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.new_configuration_popup, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, popupWidthDp(applicationContext, 2.5f), ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(repertoireView, Gravity.CENTER, 0, -100)
        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            val configurationName = popupView.findViewById<TextInputEditText>(R.id.configuration_name_input).text.toString().trim()
            val color = !popupView.findViewById<SwitchCompat>(R.id.color_switch).isChecked
            if (configurationName == DEFAULT_CONFIGURATION_NAME) {
                // TODO make toast for invalid configuration name
            } else {
                repertoireManager.newConfiguration(configurationName, color)
                storeConfigurations(applicationContext)
                updateConfigurationsMenu()
                popupWindow.dismiss()
            }
        }
        popupView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            popupWindow.dismiss()
        }
    }

    fun deleteConfigurationButton(view: View) {
        if (repertoireManager.currentConfigurationName != DEFAULT_CONFIGURATION_NAME) {
            makeConfirmationDialog(applicationContext, layoutInflater, repertoireView, "Delete Configuration: ${repertoireManager.currentConfigurationName}?")
            {
                repertoireManager.deleteConfiguration()
                storeConfigurations(applicationContext)
                updateConfigurationsMenu()
            }
        } else {
            // TODO make toast, default cannot be deleted
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
        configurationViewAdapter.notifyDataSetChanged()
        configurationsView.setSelection(configurationViewAdapter.getPosition(repertoireManager.configuration))
    }

}