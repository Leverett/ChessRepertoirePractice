package com.leverett.chessrepertoirepractice.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.leverett.chessrepertoirepractice.R

fun makeConfirmationDialog(context: Context, layoutInflater: LayoutInflater, anchorView: View, message: String, action: () -> Unit) {
    val popupView = layoutInflater.inflate(R.layout.confirmation_dialog, null) as ConstraintLayout
    val popupWindow = PopupWindow(popupView, popupWidthDp(context, 2f), ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
    val messageView = popupView.findViewById<TextView>(R.id.message)
    messageView.text = message
    messageView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)

    popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
        action()
        popupWindow.dismiss()
    }
    popupView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
        popupWindow.dismiss()
    }
}

fun makeAccountInfoPopup(context: Context, layoutInflater: LayoutInflater, anchorView: View, action: (() -> Unit)? = null) {
    val accountInfo = AccountInfo
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
        storeAccountInfo(context)
        if (!accountInfo.incompleteInfo && action != null) {
            action()
        }
        popupWindow.dismiss()
    }
    popupView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
        popupWindow.dismiss()
    }
}