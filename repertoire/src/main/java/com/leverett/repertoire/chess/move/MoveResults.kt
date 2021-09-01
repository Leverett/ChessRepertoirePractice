package com.leverett.repertoire.chess.move

import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.lines.Book
import com.leverett.rules.chess.representation.Move
import com.leverett.repertoire.chess.move.MoveResult.*
import com.leverett.repertoire.chess.settings.PlaySettings
import com.leverett.rules.chess.representation.log

class MoveResults() {

    private val repertoireManager = RepertoireManager
    private val playSettings: PlaySettings
        get() = repertoireManager.playSettings

    constructor(nextMoveResults: Map<Move, MoveResult>, movesToLineMoves: Map<Move,MutableList<LineMove>>): this() {
        this.nextMoveResults.putAll(nextMoveResults)
        this.movesToLineMoves.putAll(movesToLineMoves)
    }

    constructor(lineMoves: Collection<LineMove>, playerMove: Boolean): this() {
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

    private val nextMoveResults: MutableMap<Move, MoveResult> = mutableMapOf()
    private val movesToLineMoves = mutableMapOf<Move,MutableList<LineMove>>()

    private val bestMoves: List<Move>
        get() = movesToLineMoves.entries.filter{ it.value.any{lineMove -> lineMove.best} }.map{ it.key }

    private val preferredMoves: List<Move>
        get() = movesToLineMoves.entries.filter{ it.value.any{lineMove -> lineMove.preferred} }.map{ it.key }

    fun quickDisplay(): String {
        return nextMoveResults.entries.joinToString(",", transform = { it.key.toString() + ": " + it.value })
    }

    fun getMoveResult(move: Move): MoveResult? {
        return nextMoveResults[move]
    }

    private fun getMovesForResult(moveResult: MoveResult): Collection<Move> {
        return nextMoveResults.filter{it.value == moveResult}.keys
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

    fun getValidMoveDescriptionText(move: Move, playerMove: Boolean): String {
        var displayText = ""
        val linesForMove = movesToLineMoves[move]!!
        if (linesForMove.any{it.best}) {
            displayText += "Best move chosen\n"
        } else if (playerMove && linesForMove.any{it.preferred}) {
            displayText += "Preferred move chosen\n"
        }
        displayText += getMoveDescriptionText(move)

        val otherBestMoves = bestMoves.filter{it != move}
        if (otherBestMoves.isNotEmpty()) {
            displayText += ("\n Other best moves available: " + getOtherMoveDisplayText(move, otherBestMoves))
        }
        val otherPreferredMoves = preferredMoves.filter{it != move}
        if (playerMove && otherPreferredMoves.isNotEmpty()) {
            displayText += ("\n Other preferred moves available: " + getOtherMoveDisplayText(move, otherPreferredMoves))
        }
        return displayText
    }

    fun getMoveDescriptionText(move: Move): String {
        val lineMoves = movesToLineMoves[move]!!
        val movesWithDescription = lineMoves.filter{!it.moveDetails.description.isNullOrEmpty()}
        if (movesWithDescription.size == 1) {
            return movesWithDescription[0].moveDetails.description!!
        }
        return movesWithDescription.joinToString(separator = "\n", transform = {
            if (it.moveDetails.description == null) ""
            else it.chapter.fullName + " - " + it.moveDetails.description})

    }

    private fun getOtherMoveDisplayText(move: Move, moves: Collection<Move>): String {
        if (moves.size > 1) {
            val otherMoves = moves.filter{it != move}
            val otherMoveText = joinLineMoves(otherMoves)
            return if (otherMoveText.isNotEmpty()) otherMoveText else ""
        }
        return ""
    }

    fun getOptionsText(): String {
        for (entry in nextMoveResults.entries) {
            log("getOptionsText", entry.key.toString() + ": " + entry.value.name)
        }
        val validOptions = getMovesForResult(VALID)
        for (option in validOptions) {
            log("getOptionsText", "option: $option")
        }
        val correctOptions = getMovesForResult(CORRECT)
        return if (correctOptions.isNotEmpty()) {
            combineOptions(correctOptions)
        } else {
            combineOptions(getMovesForResult(VALID))
        }
    }

    private fun combineOptions(moves: Collection<Move>): String {
        var result = ""
        val filteredMap = movesToLineMoves.filter { moves.contains(it.key) }
        val bestMoves = filteredMap.filter{it.value.any{lm -> lm.best}}.keys
        if (bestMoves.isNotEmpty()) {
            result += "Best moves:\n" + joinLineMoves(bestMoves)
        }
        val preferredMoves = filteredMap.filter{it.value.any{lm -> lm.preferred}}.keys
        if (preferredMoves.isNotEmpty()) {
            result += "Preferred moves:\n" + joinLineMoves(preferredMoves)
        }
        if (bestMoves.isEmpty() && preferredMoves.isEmpty()) {
            val theoryMoves = filteredMap.filter{it.value.any{lm -> lm.theory}}.keys
            val otherMoves = filteredMap.filter {!theoryMoves.contains(it.key)}.keys
            if (theoryMoves.isNotEmpty()) {
                result += "Theory moves:\n" + joinLineMoves(theoryMoves)
                if (otherMoves.isNotEmpty()) {
                    result += "\nOther available moves:\n"
                }
            } else {
                result += "Available moves:\n"
            }
            if (otherMoves.isNotEmpty()) {
                result += joinLineMoves(otherMoves)
            }
        }
        return result
    }

    private fun joinLineMoves(moves: Collection<Move>): String {
        return moves.joinToString("\n") {makeMoveOptionText(it, movesToLineMoves[it]!![0]!!.algMove)}
    }

    private fun makeMoveOptionText(move: Move, algMove: String): String {
        val bookToLineMoves: MutableMap<Book, MutableList<LineMove>> = mutableMapOf()
        val standaloneLineMoves: MutableList<LineMove> = mutableListOf()
        movesToLineMoves[move]!!.forEach{
            val chapter = it.chapter
            if (chapter.isStandalone()) {
                standaloneLineMoves.add(it)
            } else {
                val book = chapter.book!!
                if (bookToLineMoves[book] == null) {
                    bookToLineMoves[book] = mutableListOf(it)
                } else {
                    bookToLineMoves[book]!!.add(it)
                }
            }
        }
        var result = "$algMove ("
        result += bookToLineMoves.entries.joinToString(", ") { if (it.value.size > 1) it.key.name else it.value[0]!!.chapter.fullName }
        result += standaloneLineMoves.joinToString(", ") { it.chapter.fullName }
        result += ")"
        return result
    }

    fun getOpponentMove(playSettings: PlaySettings): Move? {
        val validMoves = mutableListOf<Move>()
        validMoves.addAll(getMovesForResult(CORRECT))
        if (validMoves.isEmpty()) validMoves.addAll(getMovesForResult(VALID))
        if (playSettings.opponentMistakes) validMoves.addAll(getMovesForResult(CORRECT))
        return if (validMoves.isEmpty()) null else validMoves.random()
    }

    fun copy(): MoveResults {
        return MoveResults(nextMoveResults.toMap(), movesToLineMoves.toMap())
    }

}