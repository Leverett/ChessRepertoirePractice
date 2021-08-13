package com.leverett.chessrepertoirepractice.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.findFragment
import com.leverett.chessrepertoirepractice.BoardFragment
import com.leverett.chessrepertoirepractice.BoardViewModel
import com.leverett.rules.chess.representation.PieceEnum

/**
 * TODO: document your custom view class.
 */
class PromotionPopup(context: Context, private val viewModel: BoardViewModel, private val endCoords: Pair<Int,Int>) :
    ConstraintLayout(context) {

    private val activeColor = viewModel.activeColor
    private val pieceStyle = viewModel.pieceStyle
    private val boardFragment = findFragment<BoardFragment>()

    private val baseLayoutParams = LayoutParams(0, 0).also {
        it.dimensionRatio = boardFragment.squareDimensions
        it.bottomToBottom = this.id
        it.topToTop = this.id
    }

    init {
        val knightView = getImageView(PieceEnum.PieceType.KNIGHT)
        val bishopView = getImageView(PieceEnum.PieceType.BISHOP)
        val rookView = getImageView(PieceEnum.PieceType.ROOK)
        val queenView = getImageView(PieceEnum.PieceType.QUEEN)

        val knightLayoutParams = LayoutParams(baseLayoutParams)
        knightLayoutParams.leftToLeft = this.id
        knightLayoutParams.rightToLeft = bishopView.id
        knightView.layoutParams = knightLayoutParams

        val bishopLayoutParams = LayoutParams(baseLayoutParams)
        bishopLayoutParams.leftToRight = knightView.id
        bishopLayoutParams.rightToLeft = rookView.id
        bishopView.layoutParams = bishopLayoutParams

        val rookLayoutParams = LayoutParams(baseLayoutParams)
        rookLayoutParams.leftToRight = bishopView.id
        rookLayoutParams.rightToLeft = queenView.id
        rookView.layoutParams = rookLayoutParams

        val queenLayoutParams = LayoutParams(baseLayoutParams)
        queenLayoutParams.leftToRight = rookView.id
        queenLayoutParams.rightToRight = this.id

        this.setBackgroundColor(Color.WHITE)
    }

    private fun getImageView(pieceType: PieceEnum.PieceType): ImageView {
        val imageView = ImageView(context)
        val imageResource = pieceStyle.getPieceImageResource(PieceEnum.getPiece(activeColor, pieceType))
        imageView.setImageResource(imageResource!!)
        imageView.id = pieceType.ordinal
        setOnClickListener(imageView, pieceType)
        return imageView
    }

    private fun setOnClickListener(view: ImageView, pieceType: PieceEnum.PieceType) {
        view.setOnClickListener {
            boardFragment.processMoveSelection(endCoords, PieceEnum.Companion.getPiece(activeColor, pieceType))
        }
    }


    /**
     * In the example view, this drawable is drawn above the text.
     */
//    var exampleDrawable: Drawable? = null
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        // TODO: consider storing these as member variables to reduce
//        // allocations per draw cycle.
//        val paddingLeft = paddingLeft
//        val paddingTop = paddingTop
//        val paddingRight = paddingRight
//        val paddingBottom = paddingBottom
//
//        val contentWidth = width - paddingLeft - paddingRight
//        val contentHeight = height - paddingTop - paddingBottom
//
//        exampleString?.let {
//            // Draw the text.
//            canvas.drawText(
//                it,
//                paddingLeft + (contentWidth - textWidth) / 2,
//                paddingTop + (contentHeight + textHeight) / 2,
//                textPaint
//            )
//        }
//
//        // Draw the example drawable on top of the text.
//        exampleDrawable?.let {
//            it.setBounds(
//                paddingLeft, paddingTop,
//                paddingLeft + contentWidth, paddingTop + contentHeight
//            )
//            it.draw(canvas)
//        }
//    }
}