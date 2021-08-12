package com.leverett.rules.chess.representation

class Castling(var whiteKingside: Boolean,
               var whiteQueenside: Boolean,
               var blackKingside: Boolean,
               var blackQueenside: Boolean) {

    fun castleAvailable(color: Boolean, side: Boolean): Boolean {
        if (color && side) {
            return whiteKingside
        }
        if (color && !side) {
            return whiteQueenside
        }
        if (!color && side) {
            return blackKingside
        }
        if (!color && !side) {
            return blackQueenside
        }
        return false
    }

    companion object {
        val KING_HOME_FILE = 4

        val WHITE_KINGSIDE_ROOK_HOME_COORD = Pair(GRID_SIZE - 1, 0)
        val WHITE_QUEENSIDE_ROOK_HOME_COORD = Pair(GRID_SIZE - 1, GRID_SIZE - 1)
        val BLACK_KINGSIDE_ROOK_HOME_COORD = Pair(0, 0)
        val BLACK_QUEENSIDE_ROOK_HOME_COORD = Pair(0, GRID_SIZE - 1)

        val KINGSIDE_KING_DESTINATION_FILE = 6
        val KINGSIDE_ROOK_DESTINATION_FILE = 5
        val QUEENSIDE_KING_DESTINATION_FILE = 2
        val QUEENSIDE_ROOK_DESTINATION_FILE = 3
    }

    fun copy(): Castling {
        return Castling(whiteKingside, whiteQueenside, blackKingside, blackQueenside)
    }
}