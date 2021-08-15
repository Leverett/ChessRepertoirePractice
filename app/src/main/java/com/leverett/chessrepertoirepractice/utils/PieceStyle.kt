package com.leverett.chessrepertoirepractice.utils

import com.leverett.chessrepertoirepractice.R
import com.leverett.rules.chess.representation.PieceEnum

enum class PieceStyle(private val whitePawn: Int,
                      private val whiteKnight: Int,
                      private val whiteBishop: Int,
                      private val whiteRook: Int,
                      private val whiteQueen: Int,
                      private val whiteKing: Int,
                      private val blackPawn: Int,
                      private val blackKnight: Int,
                      private val blackBishop: Int,
                      private val blackRook: Int,
                      private val blackQueen: Int,
                      private val blackKing: Int
) {
    STANDARD(
        R.drawable.whitepawn,
        R.drawable.whiteknight,
        R.drawable.whitebishop,
        R.drawable.whiterook,
        R.drawable.whitequeen,
        R.drawable.whiteking,
        R.drawable.blackpawn,
        R.drawable.blackknight,
        R.drawable.blackbishop,
        R.drawable.blackrook,
        R.drawable.blackqueen,
        R.drawable.blackking);


    fun getPieceImageResource(piece: PieceEnum) : Int? {
        return when (piece) {
            PieceEnum.WHITE_PAWN -> whitePawn
            PieceEnum.WHITE_KNIGHT -> whiteKnight
            PieceEnum.WHITE_BISHOP -> whiteBishop
            PieceEnum.WHITE_ROOK -> whiteRook
            PieceEnum.WHITE_QUEEN -> whiteQueen
            PieceEnum.WHITE_KING -> whiteKing
            PieceEnum.BLACK_PAWN -> blackPawn
            PieceEnum.BLACK_KNIGHT -> blackKnight
            PieceEnum.BLACK_BISHOP -> blackBishop
            PieceEnum.BLACK_ROOK -> blackRook
            PieceEnum.BLACK_QUEEN -> blackQueen
            PieceEnum.BLACK_KING -> blackKing
            else -> null
        }
    }
}