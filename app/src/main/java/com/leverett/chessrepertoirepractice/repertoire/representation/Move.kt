package com.leverett.chessrepertoirepractice.repertoire.representation

class Move(val startLoc: Pair<Int,Int>, val endLoc: Pair<Int,Int>, val promotion: Char) {

    companion object {
        val WHITE_KINGSIDE_CASTLE: Move = Move(Pair(-1, -1), Pair(-1, -1), '0')
        val WHITE_QUEENSIDE_CASTLE: Move = Move(Pair(-1, -1), Pair(-1, -1), '1')
        val BLACK_KINGSIDE_CASTLE: Move = Move(Pair(-1, -1), Pair(-1, -1), '2')
        val BLACK_QUEENSIDE_CASTLE: Move = Move(Pair(-1, -1), Pair(-1, -1), '3')
    }
}