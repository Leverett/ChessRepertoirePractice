package com.leverett.rules.chess.representation

import com.leverett.rules.chess.parsing.NO_CASTLING

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

    fun copy(): Castling {
        return Castling(whiteKingside, whiteQueenside, blackKingside, blackQueenside)
    }

    override fun toString(): String {
        var result = ""
        if (whiteKingside) result += WHITE_KING_CHAR
        if (whiteQueenside) result += WHITE_QUEEN_CHAR
        if (blackKingside) result += BLACK_KING_CHAR
        if (blackQueenside) result += BLACK_QUEEN_CHAR
        return if (result.isEmpty()) NO_CASTLING.toString() else result

    }
}

const val KING_HOME_FILE = 4

val WHITE_KINGSIDE_ROOK_HOME_COORD = Pair(GRID_SIZE - 1, 0)
val WHITE_QUEENSIDE_ROOK_HOME_COORD = Pair(GRID_SIZE - 1, GRID_SIZE - 1)
val BLACK_KINGSIDE_ROOK_HOME_COORD = Pair(0, 0)
val BLACK_QUEENSIDE_ROOK_HOME_COORD = Pair(0, GRID_SIZE - 1)

const val KINGSIDE_KING_DESTINATION_FILE = 6
const val KINGSIDE_ROOK_DESTINATION_FILE = 5
const val QUEENSIDE_KING_DESTINATION_FILE = 2
const val QUEENSIDE_ROOK_DESTINATION_FILE = 3