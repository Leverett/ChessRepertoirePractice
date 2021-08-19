package com.leverett.repertoire.chess.mode

import com.leverett.repertoire.chess.lines.LineMove
import com.leverett.rules.chess.representation.Move
import com.leverett.repertoire.chess.mode.MoveResult.*
import com.leverett.rules.chess.representation.log

class MoveResults(lineMoves: Collection<LineMove>, playSettings: PlaySettings, playerMove: Boolean) {

    private val nextMoveResults: MutableMap<Move, MoveResult> = mutableMapOf()
    private val movesToLineMoves = mutableMapOf<Move,MutableList<LineMove>>()

    private val bestMoves: List<Move>
        get() = movesToLineMoves.entries.filter{ it.value.any{lineMove -> lineMove.best} }.map{ it.key }

    private val preferredMoves: List<Move>
        get() = movesToLineMoves.entries.filter{ it.value.any{lineMove -> lineMove.preferred} }.map{ it.key }

    fun quickDisplay(): String {
        return nextMoveResults.entries.joinToString(",", transform = { it.key.toString() + ": " + it.value })
    }

    init {
        for (nextLineMove in lineMoves) {
            val lineMoveSet = movesToLineMoves[nextLineMove.move]
            if (lineMoveSet != null) {
                lineMoveSet.add(nextLineMove)
            } else {
                movesToLineMoves[nextLineMove.move] = mutableListOf(nextLineMove)
            }
        }
        for (entry in movesToLineMoves) {
            var lineMoveResult = VALID
            for (lineMove in entry.value) {
                lineMoveResult = playSettings.categorizeLineMove(lineMove, lineMoves, playerMove)
                if (lineMoveResult == MISTAKE || lineMoveResult == CORRECT) {
                    break
                }
            }
            nextMoveResults[entry.key] = lineMoveResult
        }
    }

    fun getMoveResult(move: Move): MoveResult? {
        log("getMoveResult", "move being queried: $move")
        log("getMoveResult", "moves here: " + quickDisplay())
        return nextMoveResults[move]
    }

    fun getMovesForResult(moveResult: MoveResult): Collection<Move> {
        return nextMoveResults.filter{it.value == moveResult}.keys
    }

    private fun getLinesMovesForMove(move: Move): List<LineMove> {
        return movesToLineMoves[move]!!
    }

    fun getCorrectMoveDescriptionText(move: Move): String {
        var descriptionText = getMoveDescriptionText(move)

        val correctMoves = getMovesForResult(CORRECT)
        val otherMovesDescription = getOtherMoveDisplayText(move, correctMoves)
        if (otherMovesDescription.isNotEmpty()) {
            if (descriptionText.isNotEmpty()) {
                descriptionText += "\n"
            }
            descriptionText += "Other moves: $otherMovesDescription"
        }
        return descriptionText
    }

    fun getValidMoveDescriptionText(move: Move): String {
        var displayText = ""
        val linesForMove = movesToLineMoves[move]!!
        if (linesForMove.any{it.best}) {
            displayText += "Best move chosen\n"
        } else if (linesForMove.any{it.preferred}) {
            displayText += "Preferred move chosen\n"
        }
        displayText += getMoveDescriptionText(move)

        val otherBestMoves = bestMoves.filter{it != move}
        if (otherBestMoves.isNotEmpty()) {
            displayText += ("\n Other best moves available: " + getOtherMoveDisplayText(move, otherBestMoves))
        }
        val otherPreferredMoves = preferredMoves.filter{it != move}
        if (otherPreferredMoves.isNotEmpty()) {
            displayText += ("\n Other preferred moves available: " + getOtherMoveDisplayText(move, otherPreferredMoves))
        }
        return displayText
    }

    private fun getMoveDescriptionText(move: Move): String {
        val lineMoves = movesToLineMoves[move]!!
        val movesWithDescription = lineMoves.filter{!it.moveDetails.description.isNullOrEmpty()}
        if (movesWithDescription.size == 1) {
            return movesWithDescription[0].moveDetails.description!!
        }
        return movesWithDescription.joinToString(separator = "\n", transform = {
            if (it.moveDetails.description == null) ""
            else it.lineTree.name + ": " + it.moveDetails.description})

    }

    private fun getOtherMoveDisplayText(move: Move, moves: Collection<Move>): String {
        if (moves.size > 1) {
            val otherMoves = moves.filter{it != move}
            val otherMoveText = otherMoves.joinToString(separator = ", ", transform =
                { val otherLineMoves = getLinesMovesForMove(it);
                    otherLineMoves[0].algMove + otherLineMoves.joinToString(", ", "(", ")", transform = {lm -> lm.lineTree.name})
                }
            )
            return if (otherMoveText.isNotEmpty()) otherMoveText else ""
        }
        return ""

    }

}