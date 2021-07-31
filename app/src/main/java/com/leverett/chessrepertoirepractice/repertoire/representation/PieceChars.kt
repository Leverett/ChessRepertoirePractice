package com.leverett.chessrepertoirepractice.repertoire.representation

object PieceChars {
    const val WHITE_PAWN = 'P'
    const val BLACK_PAWN = 'p'
    const val WHITE_KNIGHT = 'N'
    const val BLACK_KNIGHT = 'n'
    const val WHITE_BISHOP = 'B'
    const val BLACK_BISHOP = 'b'
    const val WHITE_ROOK = 'R'
    const val BLACK_ROOK = 'r'
    const val WHITE_QUEEN = 'Q'
    const val BLACK_QUEEN = 'q'
    const val WHITE_KING = 'K'
    const val BLACK_KING = 'k'

    const val PAWN = 'P'
    const val KNIGHT = 'N'
    const val BISHOP = 'B'
    const val ROOK = 'R'
    const val QUEEN = 'Q'
    const val KING = 'K'
    const val EMPTY = ' '

    val PROMOTION_CHARS = charArrayOf(KNIGHT, BISHOP, ROOK, QUEEN)
}