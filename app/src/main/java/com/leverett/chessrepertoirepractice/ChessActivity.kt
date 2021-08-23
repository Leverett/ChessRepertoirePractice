package com.leverett.chessrepertoirepractice

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.leverett.chessrepertoirepractice.utils.BoardStyle
import com.leverett.chessrepertoirepractice.utils.PieceStyle
import com.leverett.rules.chess.representation.Move

abstract class ChessActivity : AppCompatActivity() {

    abstract val boardId: Int
    open val boardFragment: BoardFragment
        get() = supportFragmentManager.findFragmentById(boardId) as BoardFragment
    open val boardViewModel: BoardViewModel
        get() = boardFragment.viewModel

    abstract fun handleMove(move: Move?, undo:Boolean = false)

    fun undoMove(view: View) {
        boardViewModel.canMove = true
        boardFragment.undoMove()
    }
    fun redoNextMove(view: View) {
        boardFragment.redoNextMove()
    }
    open fun switchPerspective(view: View) {
        boardFragment.switchPerspective(view)
    }

    fun boardSettingsButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.board_settings_popup_layout, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        val boardStyleSpinner = popupView.findViewById(R.id.board_style_spinner) as Spinner
        val boardStyleSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, BoardStyle.values())
        boardStyleSpinner.adapter = boardStyleSpinnerAdapter
        boardStyleSpinner.setSelection(boardStyleSpinnerAdapter.getPosition(boardViewModel.boardStyle))
        val pieceStyleSpinner = popupView.findViewById(R.id.piece_style_spinner) as Spinner
        val pieceStyleSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, PieceStyle.values())
        pieceStyleSpinner.adapter = pieceStyleSpinnerAdapter
        pieceStyleSpinner.setSelection(pieceStyleSpinnerAdapter.getPosition(boardViewModel.pieceStyle))
        popupWindow.showAtLocation(boardFragment.view, Gravity.CENTER, 0, 0)

        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            boardViewModel.boardStyle = boardStyleSpinner.selectedItem as BoardStyle
            boardViewModel.pieceStyle = pieceStyleSpinner.selectedItem as PieceStyle
            boardFragment.updateBoardView()

            val sharedPref = this?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putString(getString(R.string.board_style_pref_key), boardViewModel.boardStyle.name)
                putString(getString(R.string.piece_style_pref_key), boardViewModel.pieceStyle.name)
                apply()
            }

            popupWindow.dismiss()
        }
    }

}