package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.PieceEnum.BLACK_ROOK
import com.leverett.rules.chess.representation.PieceEnum.WHITE_ROOK

class Rook(i: Int, j: Int) : LineMover(i, j, ROOK_DIRECTIONS) {
    companion object {
        val ROOK_DIRECTIONS: Array<Pair<Int,Int>> = arrayOf(
            Pair(-1, 0),
            Pair(0, -1),
            Pair(0, 1),
            Pair(1, 0)
        )
    }

    override fun threateningPiece(color: Boolean): PieceEnum {
        return if (color) WHITE_ROOK else BLACK_ROOK
    }
}