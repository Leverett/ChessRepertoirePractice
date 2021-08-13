package com.leverett.rules.chess.representation

import com.leverett.rules.chess.parsing.STARTING_FEN
import com.leverett.rules.chess.parsing.positionFromFen

const val GRID_SIZE = 8
val NO_ENPASSANT_TARGET_COORDINATE = Pair(-1,-1)
private const val testingFen: String = STARTING_FEN //TODO remember to remove this
fun startingPosition(): Position {
    return positionFromFen(testingFen)
}

fun newPlacements(): Array<Array<PieceEnum>> {
    return Array(GRID_SIZE) { Array(GRID_SIZE) { PieceEnum.EMPTY } }
}

class Position(val placements:Array<Array<PieceEnum>>,
               val activeColor: Boolean,
               val castling: Castling,
               val enPassantTarget: Pair<Int,Int>,
               val turn: Int) {

    fun castleAvailable(side: Boolean): Boolean {
        return castling.castleAvailable(activeColor, side)
    }

    fun copyPlacements(): Array<Array<PieceEnum>> {
        val newPlacements = newPlacements()
        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {
                newPlacements[i][j] = placements[i][j]
            }
        }
        return newPlacements
    }

    fun quickDisplay(): String {
        var result = "Board\n"
        for (j in 0 until GRID_SIZE) {
            for (i in 0 until GRID_SIZE) {
                result += placements[GRID_SIZE - 1 - i][GRID_SIZE - 1 - j]
            }
            result += "\n"
        }
        return result
    }
}