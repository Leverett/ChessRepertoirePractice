package com.leverett.chessrepertoirepractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.leverett.chessrepertoirepractice.utils.deleteLineTreeFile
import com.leverett.chessrepertoirepractice.utils.setupRepertoireManager
import com.leverett.chessrepertoirepractice.utils.storeConfigurations
import com.leverett.chessrepertoirepractice.utils.storeRepertoire
import com.leverett.repertoire.chess.RepertoireManager

class MainActivity : AppCompatActivity() {

    private val repertoireManager = RepertoireManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repertoireManager.setupStorageFunctions(
            {storeRepertoire(this.applicationContext)},
            {storeConfigurations(this.applicationContext)},
            {lineTree -> deleteLineTreeFile(this.applicationContext, lineTree)})
        setupRepertoireManager(this.applicationContext)
    }

    fun sandboxModeButton(view: View) {
        val intent = Intent(this, SandboxActivity::class.java)
        startActivity(intent)
    }

    fun repertoireModeButton(view: View) {
        val intent = Intent(this, RepertoireActivity::class.java)
        startActivity(intent)
    }

    fun practiceModeButton(view: View) {
        val intent = Intent(this, PracticeActivity::class.java)
        startActivity(intent)
    }
}