package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.PieceEnum

class Knight(i: Int, j: Int) : SquareMover(i, j) {

    override val pieceType: PieceEnum.PieceType
        get() = PieceEnum.PieceType.KNIGHT

    override val directions: Array<Pair<Int,Int>>
        get() = arrayOf(
            Pair(-1, -1),
            Pair(-1, 1),
            Pair(1, -1),
            Pair(1, 1))

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
}