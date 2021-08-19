package com.leverett.repertoire.chess.lines

import com.leverett.rules.chess.representation.Position
import com.leverett.rules.chess.representation.log

class Chapter(name: String, description: String? = null) : LineTreeBase(name, description), LineTree {

    private val statelessHashToPosition: MutableMap<String, MutableList<Position>> = mutableMapOf() // unlikely to ever have more than one state but who knows
    private val positionHashToMoves: MutableMap<String, MutableList<LineMove>> = mutableMapOf()

    override fun getMoves(position: Position): List<LineMove> {
        val moves = mutableListOf<LineMove>()
        val positions = statelessHashToPosition[position.statelessPositionHash]
        if (positions != null) {
            for (position in positions) {
                val lineMoves = positionHashToMoves[position.fen]
                if (lineMoves != null) {
                    moves.addAll(lineMoves)
                }
            }
        }
        return moves
    }

    fun addMove(move: LineMove) {
        val previousPosition = move.previousPosition
        val positionHash = previousPosition.statelessPositionHash
        val knownEquivalentPositions = statelessHashToPosition[positionHash]
        if (knownEquivalentPositions != null && !knownEquivalentPositions.contains(previousPosition)) {
            knownEquivalentPositions.add(previousPosition)
        } else {
            statelessHashToPosition[positionHash] = mutableListOf(previousPosition)
        }
        val moves = positionHashToMoves[previousPosition.fen]
        if (moves != null && !moves.contains(move)) {
            moves.add(move)
        } else {
            positionHashToMoves[previousPosition.fen] = mutableListOf(move)
        }

        val nextPosition = move.nextPosition
        val nextPositionHash = nextPosition.statelessPositionHash
        val nextPositions = statelessHashToPosition[nextPositionHash]
        if (nextPositions != null && !nextPositions.contains(nextPosition)) {
            nextPositions.add(nextPosition)
        } else {
            statelessHashToPosition[nextPositionHash] = mutableListOf(nextPosition)
        }

    }

    fun removeMove(move: LineMove) {
        val previousPosition = move.previousPosition
        val moves = positionHashToMoves[previousPosition.fen]
        if (moves != null && moves.contains(move)) {
            moves.remove(move)
            if (moves.isEmpty()) {
                positionHashToMoves.remove(previousPosition.fen)
            }
        }

        val nextPosition = move.nextPosition
        var otherWaysToPosition = false
        for (entry in positionHashToMoves) {
            for (m in entry.value) {
                if (m.nextPosition == nextPosition) {
                    otherWaysToPosition = true
                }
            }
        }
        if (!otherWaysToPosition) {
            val positionHash = nextPosition.statelessPositionHash
            val positions = statelessHashToPosition[positionHash].also{ states ->
                states?.remove(nextPosition)
            }
            if (positions.isNullOrEmpty()) {
                statelessHashToPosition.remove(positionHash)
            }
        }
    }

    fun quickDisplay(): String {
        var result = ""
        for (entry in positionHashToMoves) {
            result += "("
            for (move in entry.value) {
                result += move.algMove + " "
            }
            result += ")"
        }
        return result
    }


}