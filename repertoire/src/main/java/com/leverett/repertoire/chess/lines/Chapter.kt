package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.move.LineMove
import com.leverett.rules.chess.representation.Position

class Chapter(name: String, description: String? = null, val startingPositionFen: String? = null, var book: Book? = null) : LineTreeBase(name, description), LineTree {

    private val statelessHashToPosition: MutableMap<String, MutableList<Position>> = mutableMapOf() // unlikely to ever have more than one state but who knows
    private val positionHashToMoves: MutableMap<String, MutableList<LineMove>> = mutableMapOf()

    val fullName: String
        get() = if (isStandalone()) name else book!!.name + ": " + name

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
            moves.add(0, move)
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

    fun isStandalone(): Boolean {
        return book == null
    }

    fun quickDisplay(): String {
        var result = ""
        for (value in positionHashToMoves.values) {
            result += value.joinToString(" ", "(", ")")
        }
        return result
    }

    override fun copy(): LineTree {
        return Chapter(name, description, startingPositionFen, book)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Chapter) {
            return false
        }
        return this.fullName == other.fullName
    }


}