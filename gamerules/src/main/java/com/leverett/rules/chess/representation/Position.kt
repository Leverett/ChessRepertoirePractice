package com.leverett.rules.chess.representation

import com.leverett.rules.chess.parsing.fenFromPosition
import com.leverett.rules.chess.parsing.statelessFenFromPosition

class Position(val placements:Array<Array<Piece>>,
               val activeColor: Boolean,
               val castling: Castling,
               val enPassantTarget: Pair<Int,Int>?,
               val turn: Int) {

    constructor(position: Position): this(position.copyPlacements(), position.activeColor, position.castling.copy(), position.enPassantTarget?.copy(), position.turn)

    val fen: String
        get() {
            return fenFromPosition(this)
        }

    val statelessPositionHash: String
        get() {
            return statelessFenFromPosition(this)
        }

    fun castleAvailable(side: Boolean): Boolean {
        return castling.castleAvailable(activeColor, side)
    }

    fun pieceAt(location: Pair<Int,Int>): Piece {
        return placements[location.first][location.second]
    }

    fun copyPlacements(): Array<Array<Piece>> {
        val newPlacements = newPlacements()
        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {
                newPlacements[i][j] = placements[i][j]
            }
        }
        return newPlacements
    }

    fun copy(): Position {
        return Position(this)
    }

    fun quickDisplay(): String {
        return quickDisplay(placements)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Position) {
            return false
        }
        return fen == other.fen
    }

    override fun hashCode(): Int {
        return fen.hashCode()
    }
}