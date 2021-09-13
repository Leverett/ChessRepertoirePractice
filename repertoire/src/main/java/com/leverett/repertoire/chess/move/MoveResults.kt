package com.leverett.repertoire.chess.move

import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.Chapter
import com.leverett.rules.chess.representation.Move
import com.leverett.repertoire.chess.move.MoveResult.*
import com.leverett.repertoire.chess.settings.PlaySettings

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
            displayText += ("\nOther best moves available:\n" + getOtherMoveDisplayText(move, otherBestMoves))
        }
        val otherPreferredMoves = preferredMoves.filter{it != move}
        if (playerMove && otherPreferredMoves.isNotEmpty()) {
            displayText += ("\nOther preferred moves available:\n" + getOtherMoveDisplayText(move, otherPreferredMoves))
        }
        return displayText
    }

    fun getMoveDescriptionText(move: Move): String {
        val bookToLineMoves: MutableMap<Book, MutableList<LineMove>> = mutableMapOf()
        val standaloneLineMoves: MutableList<LineMove> = mutableListOf()
        sortMoveOptions(move, bookToLineMoves, standaloneLineMoves)

        var result = bookToLineMoves.entries.joinToString("\n\n") { getMoveDescriptionsTextForBook(it.key, it.value) }
        if (standaloneLineMoves.isNotEmpty()) {
            result += "\n" + standaloneLineMoves.joinToString("\n") {
                if (it.moveDetails.description.isNullOrBlank()) it.chapter.name
                else it.chapter.name + " - " + it.moveDetails.description}
        }
        return result
    }

    private fun getMoveDescriptionsTextForBook(book: Book, lineMoves: List<LineMove>): String {
        if (lineMoves.size == 1) {
            var result = ""
            val lineMove = lineMoves[0]
            result += lineMove.chapter.fullName
            if (lineMove.moveDetails.description != null) {
                result += " - " + lineMoves[0].moveDetails.description!!
            }
            return result
        }
        val descriptionsToChapters = mutableMapOf<String, MutableList<String>>()
        val descriptionlessMoveChapters = mutableListOf<String>()
        lineMoves.forEach{
            val description = it.moveDetails.description
            if (description.isNullOrBlank()) {
                descriptionlessMoveChapters.add(it.chapter.name)
            }
            else {
                val chapters = descriptionsToChapters[description]
                if (chapters != null) {
                    chapters.add(it.chapter.name)
                } else {
                    descriptionsToChapters[description] = mutableListOf(it.chapter.name)
                }
            }
        }
        if (descriptionsToChapters.isEmpty()) {
            return book.name
        }
        if (descriptionsToChapters.size == 1) {
            return book.name + " - " + descriptionsToChapters.keys.joinToString()
        }
        var result = book.name + ":\n"
        result += descriptionsToChapters.entries.joinToString("\n") {
            it.value.joinToString(", ") + " - " + it.key
        }
        if (descriptionlessMoveChapters.isNotEmpty()) {
            result += "\n" + descriptionlessMoveChapters.joinToString(", ")
        }
        return result
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
        val correctOptions = getMovesForResult(CORRECT)
        return if (correctOptions.isNotEmpty()) {
            combineOptions(correctOptions)
        } else {
            combineOptions(getMovesForResult(VALID))
        }
    }

    private fun combineOptions(moves: Collection<Move>): String {
        var result = ""
        val bestMoves = mutableSetOf<Move>()
        val preferredMoves = mutableSetOf<Move>()
        val theoryMoves = mutableSetOf<Move>()
        val otherMoves = mutableSetOf<Move>()
        sortOptions(moves, bestMoves, preferredMoves, theoryMoves, otherMoves)
        if (bestMoves.isNotEmpty()) {
            result += "Best moves:\n" + joinLineMoves(bestMoves) + "\n"
        }
        if (preferredMoves.isNotEmpty()) {
            result += "Preferred moves:\n" + joinLineMoves(preferredMoves) + "\n"
        }
        if (theoryMoves.isNotEmpty()) {
            result += "Theory moves:\n" + joinLineMoves(theoryMoves) + "\n"
        }
        if (otherMoves.isNotEmpty()) {
            result += if (otherMoves.size == moves.size) {
                "Available moves:\n"
            } else {
                "Other available moves:\n"
            }
            result += joinLineMoves(otherMoves) + "\n"
        }
        return result
    }

    private fun sortOptions(moves: Collection<Move>,
                            bestMoves: MutableSet<Move>,
                            preferredMoves: MutableSet<Move>,
                            theoryMoves: MutableSet<Move>,
                            otherMoves: MutableSet<Move>) {
        val filteredMap = movesToLineMoves.filter { moves.contains(it.key) }
        for (entry in filteredMap) {
            when {
                entry.value.any{it.best} -> bestMoves.add(entry.key)
                entry.value.any{it.preferred} -> preferredMoves.add(entry.key)
                entry.value.any{it.theory} -> theoryMoves.add(entry.key)
                else -> otherMoves.add(entry.key)
            }
        }
    }

    private fun joinLineMoves(moves: Collection<Move>): String {
        return moves.joinToString("\n") {makeMoveOptionText(it, movesToLineMoves[it]!![0]!!.algMove)}
    }

    private fun makeMoveOptionText(move: Move, algMove: String): String {
        val bookToLineMoves: MutableMap<Book, MutableList<LineMove>> = mutableMapOf()
        val standaloneLineMoves: MutableList<LineMove> = mutableListOf()
        sortMoveOptions(move, bookToLineMoves, standaloneLineMoves)
        var result = "$algMove ("
        result += bookToLineMoves.entries.joinToString(", ") { if (it.value.size > 1) it.key.name else it.value[0]!!.chapter.fullName }
        result += standaloneLineMoves.joinToString(", ") { it.chapter.fullName }
        result += ")"
        return result
    }

    private fun sortMoveOptions(move: Move, bookToLineMoves: MutableMap<Book, MutableList<LineMove>>, standaloneLineMoves: MutableList<LineMove>) {
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
    }

    fun getOpponentMove(playSettings: PlaySettings): Move? {
        val validMoves = mutableListOf<Move>()
        validMoves.addAll(getMovesForResult(CORRECT))
        if (validMoves.isEmpty()) validMoves.addAll(getMovesForResult(VALID))
        if (playSettings.opponentMistakes) validMoves.addAll(getMovesForResult(MISTAKE))
        return if (validMoves.isEmpty()) null else validMoves.random()
    }

    fun copy(): MoveResults {
        return MoveResults(nextMoveResults.toMap(), movesToLineMoves.toMap())
    }

}