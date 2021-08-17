package com.leverett.rules.chess.representation

import com.leverett.rules.chess.parsing.STARTING_FEN
import com.leverett.rules.chess.parsing.fenFromPosition
import com.leverett.rules.chess.parsing.positionFromFen
import com.leverett.rules.chess.parsing.statelessFenFromPosition

const val GRID_SIZE = 8
private const val testingFen: String = STARTING_FEN //TODO remember to remove this
fun startingPosition(): Position {
    return positionFromFen(testingFen)
}

fun newPlacements(): Array<Array<Piece>> {
    return Array(GRID_SIZE) { Array(GRID_SIZE) { Piece.EMPTY } }
}
fun isPromotionRank(rank: Int): Boolean {
    return (rank == 0 || rank == GRID_SIZE - 1)
}

fun quickDisplay(placements:Array<Array<Piece>>): String {
    var result = "Board\n"
    for (j in 0 until GRID_SIZE) {
        for (i in 0 until GRID_SIZE) {
            result += placements[GRID_SIZE - 1 - i][GRID_SIZE - 1 - j]
        }
        result += "\n"
    }
    return result
}

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
}