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