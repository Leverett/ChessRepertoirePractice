package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.move.LineMove
import com.leverett.rules.chess.representation.Position

class Chapter(val chapterName: String, description: String? = null, val startingPositionFen: String? = null, var book: Book? = null) :
    LineTreeBase(if (book != null) {"${book.name} : $chapterName"} else chapterName, description), LineTree {

    private val statelessHashToPosition: MutableMap<String, MutableList<Position>> = mutableMapOf() // unlikely to ever have more than one state but who knows
    private val positionHashToMoves: MutableMap<String, MutableList<LineMove>> = mutableMapOf()

    override fun getMoves(position: Position): List<LineMove> {
        val moves = mutableListOf<LineMove>()
        val positions = statelessHashToPosition[position.statelessPositionHash]
        if (positions != null) {
            for (p in positions) {
                val lineMoves = positionHashToMoves[p.fen]
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
        return this.name == other.name
    }


}