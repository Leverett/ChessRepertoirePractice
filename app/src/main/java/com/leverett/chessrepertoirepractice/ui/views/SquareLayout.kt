package com.leverett.chessrepertoirepractice.ui.views

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import com.leverett.chessrepertoirepractice.BoardViewModel
import com.leverett.chessrepertoirepractice.utils.*
import com.leverett.rules.chess.basic.piece.Pawn
import com.leverett.rules.chess.representation.GRID_SIZE
import com.leverett.rules.chess.representation.MoveStatus
import com.leverett.rules.chess.representation.PieceEnum

class SquareLayout(context: Context,
                   private val viewModel: BoardViewModel,
                   private val x: Int,
                   private val y: Int
                   ) : FrameLayout(context) {

    val coords = Pair(x, y)
    private val color = if ((x + y) % 2 == 0) viewModel.darkSquareColor else viewModel.lightSquareColor
    private var pieceView: ImageView? = null

    init {
        this.isClickable = true
        id = x + (y * GRID_SIZE)
        updateSquare()
        setOnClickListener()
    }

    fun updateSquare() {
        this.removeAllViews()
        val piece = viewModel.pieceAtCoords(x, y)
        if (piece != PieceEnum.EMPTY) {
            pieceView = ImageView(context)
            pieceView!!.setImageResource(PieceResource.getPieceImageResource(piece)!!)
            pieceView!!.isClickable = false
            this.addView(pieceView)
        }
        updateSquareColor()
    }

    private fun updateSquareColor() {
        val newColor = if (viewModel.activeSquareCoords == coords) viewModel.activeSquareColor else color
        setBackgroundColor(newColor)
    }

    private fun setOnClickListener() {
        this.setOnClickListener { view ->
            if (view is SquareLayout) {
                val viewCoords = view.coords
                val activeSquareCoords = viewModel.activeSquareCoords
                // No square actively selected already
                if (activeSquareCoords == null) {
                    val piece = viewModel.pieceAtCoords(viewCoords.first, viewCoords.second)
                    if (piece != PieceEnum.EMPTY && piece.color == viewModel.activeColor) {
                        viewModel.activeSquareCoords = viewCoords
                        updateSquareColor()
                    }
                }
                // A square is actively selected already
                else {
                    // The clicked square is the active square -> undo the active square status
                    if (viewCoords == activeSquareCoords) {
                        viewModel.activeSquareCoords = null
                        updateSquareColor()
                    }
                    // Check if this move requires promotion, and create a new flow to execute that move after the promotion dialog
                    else if (isValidPromotionMove(activeSquareCoords, viewCoords)) {
                        makePromotionDialog(viewCoords)
                    }
                    // Otherwise we have to see if the selections are a regular valid move
                    else {
                        val result = viewModel.findMoveAndStatus(activeSquareCoords, viewCoords)
                        val status = result.second
                        // If it is illegal we just alert but don't change anything
                        if (status == MoveStatus.ILLEGAL) {
                            doIllegalMoveReaction(viewCoords)
                        }
                        // If it is invalid, we don't do anything, so everything else is dealing with a valid move
                        else if (status != MoveStatus.INVALID) {
                            val move = result.first!!
                            if (move.capture != PieceEnum.EMPTY) {
                                playSound(CAPTURE_MOVE_SOUND)
                            }
                            viewModel.doMove(move)
                        }
                    }
                }
            }
        }
    }

    private fun isValidPromotionMove(startCoords: Pair<Int,Int>, endCoords: Pair<Int,Int>): Boolean {
        if (Pawn.isPromotionRank(endCoords.second) &&
                viewModel.pieceAtCoords(startCoords.first, startCoords.second).type == PieceEnum.PieceType.PAWN) {
            // This call will find any legal promotion move before any legal one
            val result = viewModel.findMoveAndStatus(startCoords, endCoords)
            if (result.second == MoveStatus.LEGAL) {
                return true
            }
            // But if there are only illegal promotions, we can signal it before returning false
            if (result.second == MoveStatus.ILLEGAL) {
                doIllegalMoveReaction(endCoords)
            }
        }
        return false
    }

    private fun makePromotionDialog(coords: Pair<Int,Int>) {
        //TODO promotion dialog
    }

    private fun doIllegalMoveReaction(coords: Pair<Int,Int>) {
        playSound(ILLEGAL_MOVE_SOUND)
    }

}