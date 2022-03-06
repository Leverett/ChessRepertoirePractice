package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.move.LineMove
import com.leverett.rules.chess.representation.Position
import java.lang.StringBuilder

class Chapter(val chapterName: String, description: String? = null, val startingPositionFen: String? = null, var book: Book? = null) :
    LineTreeBase(if (book != null) {"${book.name}: $chapterName"} else chapterName, description), LineTree {

    override val name: String
        get() = if (book != null) {"${book!!.name}: $chapterName"} else chapterName
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

    /**
     * This function gets the preferred moves for a given position. This is determined as the fist of:
     * 1. Any moves specifically labelled as 'preferred'
     * 2. The first non-mistake move available in the position
     * (3. Empty list)
     *
     * Which is then mapped to the algMove key, and the whole list is filtered for that algMove in case
     * there was a branch that reached the same position with a different comment that doesn't ever
     * fit one of the above criteria
     */
    fun getPreferredMoves(position: Position): List<LineMove> {
        val moves = getMoves(position)
        val preferredMoves = if (moves.isNotEmpty()) {
             moves
                .filter{it.preferred}
                .ifEmpty{
                    try {
                        listOf(moves.first{!it.mistake})
                    } catch (e: NoSuchElementException) {
                        listOf()
                    }
                }
                .map{it.algMove}
        } else {
            listOf()
        }
        // This is to catch any descriptions that might be added on alternate branches that somehow didn't get caught
        return moves.filter{preferredMoves.contains(it.algMove)}
    }

    fun isStandalone(): Boolean {
        return book == null
    }

    fun quickDisplay(): String {
        val result = StringBuilder()
        for (value in positionHashToMoves.values) {
            result.append(value.joinToString(" ", "(", ")"))
        }
        return result.toString()
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