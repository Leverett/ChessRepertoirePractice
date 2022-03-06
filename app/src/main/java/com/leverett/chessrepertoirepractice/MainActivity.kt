package com.leverett.chessrepertoirepractice

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.leverett.chessrepertoirepractice.utils.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private val signInResultLauncher = registerSignInResult(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setupRepertoireManager(applicationContext)
        setupLichessAccountInfo(applicationContext)
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

    fun signInButton(view: View) {
        signIn(this, signInResultLauncher)
    }

    fun testButton(view: View) {
        val context = this.applicationContext
        CoroutineScope(Dispatchers.IO).launch { uploadRepertoire(context) }
    }
}