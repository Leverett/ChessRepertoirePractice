package com.leverett.chessrepertoirepractice

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.leverett.rules.chess.representation.Move

abstract class ChessActivity : AppCompatActivity() {

    abstract val boardId: Int
    val boardFragment: BoardFragment
        get() = supportFragmentManager.findFragmentById(boardId) as BoardFragment

    abstract fun handleMove(move: Move)

    fun undoMove(view: View) {
        boardFragment.undoMove()
    }
    fun redoNextMove(view: View) {
        boardFragment.redoNextMove()
    }
    fun switchPerspective(view: View) {
        boardFragment.switchPerspective(view)
    }

}