package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.Piece

class Bishop(i: Int, j: Int) : LineMover(i, j) {

    override val pieceType: Piece.PieceType
        get() = Piece.PieceType.BISHOP

    override val directions: Array<Pair<Int,Int>>
        get() = arrayOf(
            Pair(-1, -1),
            Pair(-1, 1),
            Pair(1, -1),
            Pair(1, 1))
}