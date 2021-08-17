package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.Piece

class Knight(i: Int, j: Int) : SquareMover(i, j) {

    override val pieceType: Piece.PieceType
        get() = Piece.PieceType.KNIGHT

    override val directions: Array<Pair<Int,Int>>
        get() = arrayOf(
            Pair(1, 2),
            Pair(1, -2),
            Pair(-1, 2),
            Pair(-1, -2),
            Pair(2, 1),
            Pair(2, -1),
            Pair(-2, 1),
            Pair(-2, -1)
        )
}