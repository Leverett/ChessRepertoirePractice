package com.leverett.repertoire.chess.lines

import com.leverett.rules.chess.representation.Position

class Chapter(name: String, description: String? = null) : LineTreeBase(name, description), LineTree {

    private val statelessHashToPosition: MutableMap<String, MutableList<Position>> = mutableMapOf() // unlikely to ever have more than one state but who knows
    private val positionToMoves: MutableMap<Position, MutableList<LineMove>> = mutableMapOf()

    override fun getMoves(position: Position): List<LineMove> {
        val moves = mutableListOf<LineMove>()
        val positions = statelessHashToPosition[position.statelessPositionHash]
        if (positions != null) {
            for (position in positions) {
                val lineMoves = positionToMoves[position]
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
        val previousPositions = statelessHashToPosition[positionHash]
        if (previousPositions != null && !previousPositions.contains(previousPosition)) {
            previousPositions.add(previousPosition)
        } else {
            statelessHashToPosition[positionHash] = mutableListOf(previousPosition)
        }
        val moves = positionToMoves[previousPosition]
        if (moves != null && !moves.contains(move)) {
            moves.add(move)
        } else {
            positionToMoves[previousPosition] = mutableListOf(move)
        }

        val nextPosition = move.nextPosition
        val nextPositionHash = previousPosition.statelessPositionHash
        val nextPositions = statelessHashToPosition[nextPositionHash]
        if (nextPositions != null && !nextPositions.contains(nextPosition)) {
            nextPositions.add(nextPosition)
        } else {
            statelessHashToPosition[nextPositionHash] = mutableListOf(nextPosition)
        }

    }

    fun removeMove(move: LineMove) {
        val previousPosition = move.previousPosition
        val moves = positionToMoves[previousPosition]
        if (moves != null && moves.contains(move)) {
            moves.remove(move)
            if (moves.isEmpty()) {
                positionToMoves.remove(previousPosition)
            }
        }

        val nextPosition = move.nextPosition
        var otherWaysToPosition = false
        for (entry in positionToMoves) {
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


}