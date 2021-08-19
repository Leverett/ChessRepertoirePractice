package com.leverett.chessrepertoirepractice

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.leverett.rules.chess.representation.Move

abstract class ChessActivity : AppCompatActivity() {

    abstract val boardId: Int
    private val boardFragment: BoardFragment
        get() = supportFragmentManager.findFragmentById(boardId) as BoardFragment
    open val boardViewModel: BoardViewModel
        get() = boardFragment.viewModel

    abstract fun handleMove(move: Move?)

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

}