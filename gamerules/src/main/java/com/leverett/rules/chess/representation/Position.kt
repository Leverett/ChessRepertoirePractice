package com.leverett.rules.chess.representation

import com.leverett.rules.RulesEngine
import com.leverett.rules.log

class Position(val placements:Array<Array<PieceEnum>>,
               val activeColor: Boolean,
               val castling: Castling,
               val enPassantTarget: Pair<Int,Int>,
               val turn: Int) {

    @Transient private var gameStatus: GameStatus? = null

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

    fun statusCalculated(): Boolean {
        return gameStatus != null
    }

    fun gameStatus(rulesEngine: RulesEngine): GameStatus {
        if (gameStatus == null) {
            val validMoves = rulesEngine.validMoves(this)
            val legalMoves = validMoves.first
            val illegalMoves = validMoves.second
            val inCheck = rulesEngine.isInCheck(this)
            gameStatus = GameStatus(legalMoves, illegalMoves, inCheck)
        }
        return gameStatus as GameStatus
    }

    fun legalMoves(rulesEngine: RulesEngine): List<Move> {
        val result = gameStatus(rulesEngine).legalMoves
        log("POSITION legal moves", result.toString())
        return result
    }

    fun illegalMoves(rulesEngine: RulesEngine): List<Move> {
        return gameStatus(rulesEngine).illegalMoves
    }

    fun findMoveAndStatus(rulesEngine: RulesEngine, startLoc: Pair<Int,Int>, endLoc: Pair<Int,Int>): Pair<Move?, MoveStatus> {
        for (move in legalMoves(rulesEngine)) {
            if (move.startLoc == startLoc && move.endLoc == endLoc) {
                return Pair(move, MoveStatus.LEGAL)
            }
        }
        for (move in illegalMoves(rulesEngine)) {
            if (move.startLoc == startLoc && move.endLoc == endLoc) {
                return Pair(move, MoveStatus.ILLEGAL)
            }

        }
        return Pair(null, MoveStatus.INVALID)
    }

    class GameStatus(val legalMoves: List<Move>, val illegalMoves: List<Move>, val inCheck: Boolean) {

        val inCheckmate: Boolean
            get() {
                return legalMoves.isEmpty() && inCheck
            }

        val inStalemate: Boolean
            get() {
                return legalMoves.isEmpty() && !inCheck
            }

    }

    fun quickDisplay(): String {
        var result = "Board\n"
        for (j in 0 until GRID_SIZE) {
            for (i in 0 until GRID_SIZE) {
                result += placements[i][j]
            }
            result += "\n"
        }
        return result
    }
}