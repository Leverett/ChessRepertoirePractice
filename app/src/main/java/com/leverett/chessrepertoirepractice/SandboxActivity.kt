package com.leverett.chessrepertoirepractice

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout

class SandboxActivity : ChessActivity() {

    override val boardId = R.id.sandbox_board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sandbox)
    }

    fun boardSettingsButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.board_settings_popup, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(boardFragment.view, Gravity.CENTER, 0, 0)
        setupBoardSettingsOptions(popupView)
        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            popupWindow.dismiss()
        }
    }
}