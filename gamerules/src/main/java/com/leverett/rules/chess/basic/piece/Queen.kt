package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.PieceEnum

class Queen(i: Int, j: Int) : LineMover(i, j) {

    override val pieceType: PieceEnum.PieceType
        get() = PieceEnum.PieceType.QUEEN

    override val directions: Array<Pair<Int,Int>>
        get() = arrayOf(
            Pair(-1, 0),
            Pair(0, -1),
            Pair(0, 1),
            Pair(1, 0),
            Pair(-1, -1),
            Pair(-1, 1),
            Pair(1, -1),
            Pair(1, 1))

}