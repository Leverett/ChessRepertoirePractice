package com.leverett.chessrepertoirepractice.utils

import com.leverett.chessrepertoirepractice.R
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.PieceEnum.*

object PieceResource {
    fun getPieceImageResource(piece: PieceEnum) : Int? {
        return when (piece) {
            BLACK_PAWN -> R.drawable.blackpawn
            BLACK_KNIGHT -> R.drawable.blackknight
            BLACK_BISHOP -> R.drawable.blackbishop
            BLACK_ROOK -> R.drawable.blackrook
            BLACK_QUEEN -> R.drawable.blackqueen
            BLACK_KING -> R.drawable.blackking
            WHITE_PAWN -> R.drawable.whitepawn
            WHITE_KNIGHT -> R.drawable.whiteknight
            WHITE_BISHOP -> R.drawable.whitebishop
            WHITE_ROOK -> R.drawable.whiterook
            WHITE_QUEEN -> R.drawable.whitequeen
            WHITE_KING -> R.drawable.whiteking
            else -> null
        }
    }
}