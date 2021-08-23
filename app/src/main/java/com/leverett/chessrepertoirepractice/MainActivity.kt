package com.leverett.chessrepertoirepractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun sandboxModeButton(view: View) {
        val intent = Intent(this, SandboxActivity::class.java)
        startActivity(intent)
    }

    fun practiceModeButton(view: View) {
        val intent = Intent(this, PracticeActivity::class.java)
        startActivity(intent)
    }
}