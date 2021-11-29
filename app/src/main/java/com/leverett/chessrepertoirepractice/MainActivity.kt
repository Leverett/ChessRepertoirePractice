package com.leverett.chessrepertoirepractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.leverett.chessrepertoirepractice.utils.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRepertoireManager(applicationContext)
        setupAccountInfo(applicationContext)
    }

    fun sandboxModeButton(view: View) {
        val intent = Intent(this, SandboxActivity::class.java)
        startActivity(intent)
    }

    fun accountInfoButton(view: View) {
        makeAccountInfoPopup(applicationContext, layoutInflater, view)
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