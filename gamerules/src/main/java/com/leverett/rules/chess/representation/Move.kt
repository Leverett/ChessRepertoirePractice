package com.leverett.rules.chess.representation

import com.leverett.rules.chess.representation.PieceEnum.EMPTY

class Move(val startLoc: Pair<Int,Int>, val endLoc: Pair<Int,Int>, val capture: PieceEnum, val promotion: PieceEnum) {

    companion object {
        val WHITE_KINGSIDE_CASTLE: Move = Move(Pair(-1, -1), Pair(-1, -1), EMPTY, EMPTY)
        val WHITE_QUEENSIDE_CASTLE: Move = Move(Pair(-1, -1), Pair(-2, -2), EMPTY, EMPTY)
        val BLACK_KINGSIDE_CASTLE: Move = Move(Pair(-2, -2), Pair(-1, -1), EMPTY, EMPTY)
        val BLACK_QUEENSIDE_CASTLE: Move = Move(Pair(-2, -2), Pair(-2, -2), EMPTY, EMPTY)
    }
}