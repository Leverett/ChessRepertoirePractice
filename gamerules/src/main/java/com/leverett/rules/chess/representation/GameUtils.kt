package com.leverett.rules.chess.representation

import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.parsing.STARTING_FEN
import com.leverett.rules.chess.parsing.positionFromFen
import java.util.logging.Level
import java.util.logging.Logger


const val GRID_SIZE = 8
private const val testingFen: String = STARTING_FEN //TODO remember to remove this
fun startingPosition(): Position {
    return positionFromFen(testingFen)
}

fun newGameHistory(): GameHistory {
    val position = startingPosition()
    val rulesEngine = BasicRulesEngine
    val positionStatus = rulesEngine.positionStatus(position)
    return GameHistory(GameState(position, positionStatus, null, ""))
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
            result += placements[i][GRID_SIZE - 1 - j]
        }
        result += "\n"
    }
    return result
}

fun log(tag: String, message: String) {
    Logger.getLogger(tag).log(Level.SEVERE, message)
}