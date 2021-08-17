package com.leverett.chessrepertoirepractice.ui.views

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.findFragment
import com.leverett.chessrepertoirepractice.BoardFragment
import com.leverett.chessrepertoirepractice.BoardViewModel
import com.leverett.chessrepertoirepractice.utils.*
import com.leverett.rules.chess.representation.GRID_SIZE
import com.leverett.rules.chess.representation.Piece

class SquareLayout(context: Context,
                   private val viewModel: BoardViewModel,
                   x: Int,
                   y: Int
                   ) : FrameLayout(context) {

    val coords = Pair(x, y)
    private val squareColor = (x + y) % 2 != 0
    private var pieceView: ImageView? = null

    init {
        this.isClickable = true
        id = x + (y * GRID_SIZE)
        updateSquare()
        setOnClickListener()
    }

    fun updateSquare() {
        this.removeAllViews()
        val piece = viewModel.pieceAtCoords(coords)
        if (piece != Piece.EMPTY) {
            pieceView = ImageView(context)
            pieceView!!.setImageResource(viewModel.pieceStyle.getPieceImageResource(piece)!!)
            pieceView!!.isClickable = false
            this.addView(pieceView)
        }
        updateSquareColor()
    }

    fun updateSquareColor() {
        val newColor = when {
            viewModel.activeSquareCoords == coords -> viewModel.boardStyle.activeSquareColor
            squareColor -> viewModel.boardStyle.lightSquareColor
            else -> viewModel.boardStyle.darkSquareColor
        }
        setBackgroundColor(newColor)
    }

    private fun setOnClickListener() {
        this.setOnClickListener { view ->
            if (view is SquareLayout) {
                val viewCoords = view.coords
                val activeSquareCoords = viewModel.activeSquareCoords
                val piece = viewModel.pieceAtCoords(viewCoords)
                // No square actively selected already
                if (activeSquareCoords == null) {
                    if (piece != Piece.EMPTY && piece.color == viewModel.activeColor) {
                        viewModel.activeSquareCoords = viewCoords
                        updateSquareColor()
                    }
                }
                // A square is actively selected already
                else {
                    val boardFragment = findFragment<BoardFragment>()
                    when {
                        // The clicked square is the active square -> undo the active square status
                        viewCoords == activeSquareCoords -> {
                            viewModel.activeSquareCoords = null
                            updateSquareColor()
                        }
                        // The clicked square is another piece that can be moved -> switch active square to it
                        piece != Piece.EMPTY && piece.color == viewModel.activeColor -> {
                            viewModel.activeSquareCoords = viewCoords
                            boardFragment.squareAt(activeSquareCoords).updateSquareColor()
                            updateSquareColor()
                        }
                        // Check if this move requires promotion, and create a new flow to execute that move after the promotion dialog
                        boardFragment.isValidPromotionMove(activeSquareCoords, viewCoords) ->
                            boardFragment.makePromotionPopup(viewCoords)
                        // Otherwise we have to see if the selections are a regular valid move
                        else -> boardFragment.processMoveSelection(viewCoords)
                    }
                }
            }
        }
    }

    fun doIllegalMoveReaction() {
        playSound(ILLEGAL_MOVE_SOUND)
    }

}