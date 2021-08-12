package com.leverett.rules.chess.representation

class Move(val startLoc: Pair<Int,Int>, val endLoc: Pair<Int,Int>, val capture: PieceEnum,
           val promotion: PieceEnum? = null, val enPassant: Boolean = false, val castle: CastleMove? = null) {

    companion object {
        val WHITE_KINGSIDE_CASTLE: Move = Move(Pair(-1, -1), Pair(-1, -1), PieceEnum.EMPTY, castle = CastleMove.WHITE_KINGSIDE_CASTLE)
        val WHITE_QUEENSIDE_CASTLE: Move = Move(Pair(-1, -1), Pair(-2, -2), PieceEnum.EMPTY, castle = CastleMove.WHITE_QUEENSIDE_CASTLE)
        val BLACK_KINGSIDE_CASTLE: Move = Move(Pair(-2, -2), Pair(-1, -1), PieceEnum.EMPTY, castle = CastleMove.BLACK_KINGSIDE_CASTLE)
        val BLACK_QUEENSIDE_CASTLE: Move = Move(Pair(-2, -2), Pair(-2, -2), PieceEnum.EMPTY, castle = CastleMove.BLACK_QUEENSIDE_CASTLE)
    }

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