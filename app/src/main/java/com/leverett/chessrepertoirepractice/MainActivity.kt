package com.leverett.chessrepertoirepractice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.leverett.chessrepertoirepractice.utils.*
import com.leverett.repertoire.chess.external.LichessAccountInfo
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

fun makeAccountInfoPopup(context: Context, layoutInflater: LayoutInflater, anchorView: View, action: (() -> Unit)? = null) {
    val accountInfo = LichessAccountInfo
    val popupView = layoutInflater.inflate(R.layout.account_info_popup, null) as ConstraintLayout
    val popupWindow = PopupWindow(popupView, popupWidthDp(context, 2.5f), ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
    val accountNameInputView = popupView.findViewById<TextInputEditText>(R.id.account_name_input)
    val apiTokenInputView = popupView.findViewById<TextInputEditText>(R.id.api_token_input)
    if (accountInfo.accountName != null) {
        accountNameInputView.setText(accountInfo.accountName)
    }
    if (accountInfo.apiToken != null) {
        apiTokenInputView.setText(accountInfo.apiToken)
    }
    popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
        accountInfo.accountName = accountNameInputView.text.toString()
        accountInfo.apiToken = apiTokenInputView.text.toString()
        storeLichessAccountInfo(context)
        if (!accountInfo.incompleteInfo && action != null) {
            action()
        }
        popupWindow.dismiss()
    }
    popupView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
        popupWindow.dismiss()
    }
}