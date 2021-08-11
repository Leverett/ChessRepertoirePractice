package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.PieceEnum.BLACK_KNIGHT
import com.leverett.rules.chess.representation.PieceEnum.WHITE_KNIGHT
import com.leverett.rules.chess.representation.PieceEnum

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

    override fun threateningPiece(color: Boolean): PieceEnum {
        return if (color) WHITE_KNIGHT else BLACK_KNIGHT
    }
}