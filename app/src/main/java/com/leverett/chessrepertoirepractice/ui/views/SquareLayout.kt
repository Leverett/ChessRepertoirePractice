package com.leverett.chessrepertoirepractice.ui.views

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.leverett.chessrepertoirepractice.BoardViewModel
import com.leverett.chessrepertoirepractice.utils.PieceResource
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.Position

class SquareLayout(context: Context,
                   private val viewModel: BoardViewModel,
                   val i: Int,
                   val j: Int
                   ) : FrameLayout(context) {

    val coords = Pair(i, j)
    private val color = if ((i + j) % 2 == 0) viewModel.darkColor else viewModel.lightColor
    private var pieceView: ImageView? = null

    var activeSquare = false
        set(value) {
            activeSquare = value
            val newColor = if (activeSquare) viewModel.activeColor else color
            setBackgroundColor(newColor)
            field = value
        }

    init {
        id = i + (j * Position.GRID_SIZE)
        setBackgroundColor(color)
        val piece = viewModel.placements[i][j]
        if (piece != PieceEnum.EMPTY) {
            pieceView = ImageView(context)
            pieceView!!.setImageResource(PieceResource.getPieceImageResource(piece)!!)
            pieceView!!.isClickable = false
        }
    }

}