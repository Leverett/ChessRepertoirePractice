package com.leverett.chessrepertoirepractice.utils

import com.leverett.chessrepertoirepractice.R
import com.leverett.rules.chess.representation.Piece

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
    Standard(
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


    fun getPieceImageResource(piece: Piece) : Int? {
        return when (piece) {
            Piece.WHITE_PAWN -> whitePawn
            Piece.WHITE_KNIGHT -> whiteKnight
            Piece.WHITE_BISHOP -> whiteBishop
            Piece.WHITE_ROOK -> whiteRook
            Piece.WHITE_QUEEN -> whiteQueen
            Piece.WHITE_KING -> whiteKing
            Piece.BLACK_PAWN -> blackPawn
            Piece.BLACK_KNIGHT -> blackKnight
            Piece.BLACK_BISHOP -> blackBishop
            Piece.BLACK_ROOK -> blackRook
            Piece.BLACK_QUEEN -> blackQueen
            Piece.BLACK_KING -> blackKing
            else -> null
        }
    }
}