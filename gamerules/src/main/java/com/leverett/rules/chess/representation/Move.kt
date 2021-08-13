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
val WHITE_KINGSIDE_CASTLE: Move = Move(Pair(KING_HOME_FILE, 0), Pair(KINGSIDE_KING_DESTINATION_FILE, 0), PieceEnum.EMPTY, castle = Move.CastleMove.WHITE_KINGSIDE_CASTLE)
val WHITE_QUEENSIDE_CASTLE: Move = Move(Pair(KING_HOME_FILE, 0), Pair(QUEENSIDE_KING_DESTINATION_FILE, 0), PieceEnum.EMPTY, castle = Move.CastleMove.WHITE_QUEENSIDE_CASTLE)
val BLACK_KINGSIDE_CASTLE: Move = Move(Pair(KING_HOME_FILE, GRID_SIZE-1), Pair(KINGSIDE_KING_DESTINATION_FILE, GRID_SIZE-1), PieceEnum.EMPTY, castle = Move.CastleMove.BLACK_KINGSIDE_CASTLE)
val BLACK_QUEENSIDE_CASTLE: Move = Move(Pair(KING_HOME_FILE, GRID_SIZE-1), Pair(QUEENSIDE_KING_DESTINATION_FILE, GRID_SIZE-1), PieceEnum.EMPTY, castle = Move.CastleMove.BLACK_QUEENSIDE_CASTLE)