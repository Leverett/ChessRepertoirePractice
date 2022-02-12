package com.leverett.chessrepertoirepractice

import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.leverett.chessrepertoirepractice.utils.BoardStyle
import com.leverett.chessrepertoirepractice.utils.PieceStyle
import com.leverett.repertoire.chess.move.MoveDefinition

abstract class ChessActivity : AppCompatActivity() {

    abstract val boardId: Int
    open val boardFragment: BoardFragment
        get() = supportFragmentManager.findFragmentById(boardId) as BoardFragment
    open val boardViewModel: BoardViewModel
        get() = boardFragment.viewModel

    open fun handleMove(moveDefinition: MoveDefinition, undo:Boolean = false) {}

    fun undoMove(view: View) {
        boardViewModel.canMove = true
        boardFragment.undoMove()
    }
    fun redoNextMove(view: View) {
        boardFragment.redoNextMove()
    }

    open fun switchPerspective(view: View) {
        boardFragment.switchPerspective(view)
        resetActivity()
    }

    fun resetBoardButton(view: View) {
        boardFragment.reset()
        resetActivity()
    }
    open fun resetActivity() {}

    protected fun setupBoardSettingsOptions(popupView: View) {
        val boardStyleSpinner = popupView.findViewById(R.id.board_style_spinner) as Spinner
        val boardStyleSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, BoardStyle.values())
        boardStyleSpinner.adapter = boardStyleSpinnerAdapter
        boardStyleSpinner.setSelection(boardStyleSpinnerAdapter.getPosition(boardViewModel.boardStyle))
        val pieceStyleSpinner = popupView.findViewById(R.id.piece_style_spinner) as Spinner
        val pieceStyleSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, PieceStyle.values())
        pieceStyleSpinner.adapter = pieceStyleSpinnerAdapter
        pieceStyleSpinner.setSelection(pieceStyleSpinnerAdapter.getPosition(boardViewModel.pieceStyle))


        boardStyleSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                boardViewModel.boardStyle = boardStyleSpinnerAdapter.getItem(position)!!
                boardFragment.updateBoardView()
                saveBoardPreferences()
            }
        }
        pieceStyleSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                boardViewModel.pieceStyle = pieceStyleSpinnerAdapter.getItem(position)!!
                boardFragment.updateBoardView()
                saveBoardPreferences()
            }
        }
    }

    private fun saveBoardPreferences() {
        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(getString(R.string.board_style_pref_key), boardViewModel.boardStyle.name)
            putString(getString(R.string.piece_style_pref_key), boardViewModel.pieceStyle.name)
            apply()
        }
    }

}