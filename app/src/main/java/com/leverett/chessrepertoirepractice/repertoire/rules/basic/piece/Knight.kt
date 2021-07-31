package com.leverett.chessrepertoirepractice.repertoire.rules.basic.piece

import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.BLACK_KNIGHT
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.WHITE_KNIGHT

class Knight(i: Int, j: Int) : SquareMover(i, j, KNIGHT_DIRECTIONS) {
    companion object {
        val KNIGHT_DIRECTIONS: Array<Pair<Int,Int>> = arrayOf(
            Pair(1, 2),
            Pair(1, -2),
            Pair(-1, 2),
            Pair(-1, -2),
            Pair(2, 1),
            Pair(2, -1),
            Pair(-2, 1),
            Pair(-2, -1),
        )
    }

    override fun threateningPieceChar(color: Boolean): Char {
        return if (color) WHITE_KNIGHT else BLACK_KNIGHT
    }
}