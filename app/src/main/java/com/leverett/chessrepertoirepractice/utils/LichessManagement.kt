package com.leverett.chessrepertoirepractice.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.leverett.chessrepertoirepractice.R
import java.io.BufferedInputStream
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL


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

fun getStudies(accountName: String, apiToken: String, retry: Boolean = false): String {
    val url = URL("https://lichess.org/study/by/$accountName/export.pgn")
    val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
    urlConnection.setRequestProperty("Authorization", "Bearer $apiToken")
    urlConnection.setRequestProperty("Content-Type", "application/x-chess-pgn")
    val inputStream = BufferedInputStream(urlConnection.inputStream)
    val pgnBuilder = StringBuilder()
    var c: Int
    while (inputStream.read().also { c = it } != -1) {
        pgnBuilder.append(c.toChar())
    }
    urlConnection.disconnect()
    val pgn = pgnBuilder.toString()
    if (!retry && pgn.isBlank()) {
        return getStudies(accountName, apiToken, true)
    }
    return pgn
}

object LichessAccountInfo {
    var accountName: String? = "CircleBreaker"
    var apiToken: String? = "lip_jmKrmN5dH54qH1nYP73r"
//    var accountName: String? = null
//    var apiToken: String? = null

    val incompleteInfo: Boolean
        get() = accountName.isNullOrBlank() || apiToken.isNullOrBlank()
    val notSet: Boolean
        get() = accountName.isNullOrBlank() && apiToken.isNullOrBlank()
}