package com.leverett.rules.chess.representation

class Move(val startLoc: Pair<Int,Int>, val endLoc: Pair<Int,Int>, val capture: PieceEnum,
           val promotion: PieceEnum? = null, val enPassant: Boolean = false, val castle: CastleMove? = null) {

    enum class CastleMove {
        WHITE_KINGSIDE_CASTLE,
        WHITE_QUEENSIDE_CASTLE,
        BLACK_KINGSIDE_CASTLE,
        BLACK_QUEENSIDE_CASTLE
    }

    override fun toString(): String {
        return "$startLoc -> $endLoc"
    }
}