package com.leverett.repertoire.chess.move

import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.move.MoveResult.*
import com.leverett.repertoire.chess.pgn.BASELINE_CHAPTER_NAME
import com.leverett.repertoire.chess.settings.PlaySettings
import java.lang.StringBuilder

class MoveResults(private val playSettings: PlaySettings) {

    private val repertoireManager = RepertoireManager

    constructor(nextMoveResults: Map<MoveDefinition, MoveResult>, movesToLineMoves: Map<MoveDefinition,MutableList<LineMove>>, playSettings: PlaySettings): this(playSettings) {
        this.nextMoveResults.putAll(nextMoveResults)
        this.equivalentMovesMap.putAll(movesToLineMoves)
    }

    constructor(lineMoves: Collection<LineMove>, playerMove: Boolean, playSettings: PlaySettings): this(playSettings) {
        for (nextLineMove in lineMoves) {
            val lineMoveSet = equivalentMovesMap[nextLineMove.moveDefinition]
            if (lineMoveSet != null) {
                lineMoveSet.add(nextLineMove)
            } else {
                equivalentMovesMap[nextLineMove.moveDefinition] = mutableListOf(nextLineMove)
            }
        }
        for (entry in equivalentMovesMap) {
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

    private val nextMoveResults: MutableMap<MoveDefinition, MoveResult> = mutableMapOf()
    private val equivalentMovesMap = mutableMapOf<MoveDefinition,MutableList<LineMove>>()

    private val bestMoves: List<MoveDefinition> = equivalentMovesMap.entries.filter{ it.value.any{ lineMove -> lineMove.best} }.map{ it.key }
    private val preferredMoves: List<MoveDefinition>  = equivalentMovesMap.entries.filter{ it.value.any{ lineMove -> lineMove.preferred} }.map{ it.key }
    private val theoryMoves: List<MoveDefinition>  = equivalentMovesMap.entries
        .filter{ !it.value.any{lineMove -> lineMove.best || lineMove.preferred} && it.value.any{lineMove -> lineMove.theory} }.map{ it.key }
    private val otherMoves: List<MoveDefinition> = equivalentMovesMap.keys.filter { !bestMoves.contains(it) || !preferredMoves.contains(it) || theoryMoves.contains(it) }

    fun quickDisplay(): String {
        return nextMoveResults.entries.joinToString(",", transform = { it.key.toString() + ": " + it.value })
    }

    fun getMoveResult(moveDefinition: MoveDefinition): MoveResult? {
        return nextMoveResults[moveDefinition]
    }

    private fun getMovesForResult(moveResult: MoveResult): Collection<MoveDefinition> {
        return nextMoveResults.filter{it.value == moveResult}.keys
    }

    fun getMoveDescriptionText(moveDefinition: MoveDefinition): String {
        return getMoveDescriptionText(equivalentMovesMap[moveDefinition]!!)
    }

    private fun getMoveDescriptionText(lineMoves: List<LineMove>): String {
        val bookToLineMoves: MutableMap<String, MutableList<LineMove>> = mutableMapOf()
        val standaloneLineMoves: MutableList<LineMove> = mutableListOf()
        sortMoveOptions(lineMoves, bookToLineMoves, standaloneLineMoves)

        val result = StringBuilder()
        result.append(bookToLineMoves.entries.joinToString("\n") { getMoveDescriptionsTextForBook(it.key, it.value) })
        if (standaloneLineMoves.isNotEmpty()) {
            result.append("\n")
            result.append(standaloneLineMoves.joinToString("\n") {
                if (it.moveDetails.description.isNullOrBlank()) it.fullName
                else "${it.fullName} - ${it.moveDetails.description}"})
        }
        return result.toString()
    }

    private fun getMoveDescriptionsTextForBook(bookName: String, lineMoves: List<LineMove>): String {
        val result = StringBuilder()
        if (lineMoves.size == 1) {
            val lineMove = lineMoves[0]
            result.append(
                if (lineMove.chapterName == BASELINE_CHAPTER_NAME) {
                    bookName
                } else {
                    lineMove.fullName
                })
            if (lineMove.moveDetails.description != null) {
                result.append(" - ${lineMoves[0].moveDetails.description!!}")
            }
            return result.toString()
        }
        val descriptionsToChapters = mutableMapOf<String, MutableSet<String>>()
        val descriptionlessMoveChapters = mutableSetOf<String>()
        lineMoves.forEach{
            val description = it.moveDetails.description?.trim()
            if (description.isNullOrBlank()) {
                descriptionlessMoveChapters.add(it.chapterName)
            }
            else {
                val chapters = descriptionsToChapters[description]
                if (chapters != null) {
                    chapters.add(it.chapterName)
                } else {
                    descriptionsToChapters[description] = mutableSetOf(it.chapterName)
                }
            }
        }
        descriptionlessMoveChapters.removeIf { ch -> !descriptionsToChapters.keys.any { it.contentEquals(ch) } }
        if (descriptionsToChapters.isEmpty()) {
            return bookName
        }
        if (descriptionsToChapters.size == 1) {
            return "$bookName - ${descriptionsToChapters.keys.joinToString()}"
        }
        result.append("$bookName:\n")
        result.append(descriptionsToChapters.entries.joinToString("\n") {
            it.value.joinToString(", ") + " - ${it.key}"
        })
        if (descriptionlessMoveChapters.isNotEmpty()) {
            result.append("\n${descriptionlessMoveChapters.joinToString(", ")}")
        }
        return result.toString()
    }


    private fun sortMoveOptions(lineMoves: List<LineMove>, bookToLineMoves: MutableMap<String, MutableList<LineMove>>, standaloneLineMoves: MutableList<LineMove>) {
        lineMoves.forEach{
            val bookName = it.bookName
            if (bookName == null) {
                standaloneLineMoves.add(it)
            } else {
                if (bookToLineMoves[bookName] == null) {
                    bookToLineMoves[bookName] = mutableListOf(it)
                } else {
                    bookToLineMoves[bookName]!!.add(it)
                }
            }
        }
    }

    fun getCorrectMoveDescriptionText(moveDefinition: MoveDefinition): String {
        var descriptionText = getMoveDescriptionText(equivalentMovesMap[moveDefinition]!!)

        val correctMoves = getMovesForResult(CORRECT)
        val otherMovesDescription = getOtherMoveDisplayText(moveDefinition, correctMoves)
        if (otherMovesDescription.isNotEmpty()) {
            if (descriptionText.isNotEmpty()) {
                descriptionText += "\n"
            }
            descriptionText += "Other moves: $otherMovesDescription"
        }
        return descriptionText
    }

    fun getValidMoveDescriptionText(moveDefinition: MoveDefinition, playerMove: Boolean): String {
        val displayText = StringBuilder()
        val linesForMove = equivalentMovesMap[moveDefinition]!!
        if (linesForMove.any{it.best}) {
            displayText.append("Best move chosen\n")
        } else if (playerMove && linesForMove.any{it.preferred}) {
            displayText.append("Preferred move chosen\n")
        }
        displayText.append(getMoveDescriptionText(linesForMove))

        val otherBestMoves = bestMoves.filter{it != moveDefinition}
        if (otherBestMoves.isNotEmpty()) {
            displayText.append ("\nOther best moves available:\n${getOtherMoveDisplayText(moveDefinition, otherBestMoves)}")
        }
        val otherPreferredMoves = preferredMoves.filter{it != moveDefinition}
        if (playerMove && otherPreferredMoves.isNotEmpty()) {
            displayText.append ("\nOther preferred moves available:\n${getOtherMoveDisplayText(moveDefinition, otherPreferredMoves)}")
        }
        return displayText.toString()
    }

    private fun getOtherMoveDisplayText(moveDefinition: MoveDefinition, moveDefinitions: Collection<MoveDefinition>): String {
        if (moveDefinitions.size > 1) {
            val otherMoves = moveDefinitions.filter{it != moveDefinition}
            val otherMoveText = joinLineMoves(otherMoves)
            return otherMoveText.ifEmpty { "" }
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

    private fun combineOptions(moveDefinitions: Collection<MoveDefinition>): String {
        val result = StringBuilder()
        if (bestMoves.isNotEmpty()) {
            result.append("Best moves:\n${joinLineMoves(bestMoves)}\n")
        }
        if (preferredMoves.isNotEmpty()) {
            result.append("Preferred moves:\n${joinLineMoves(preferredMoves)}\n")
        }
        if (theoryMoves.isNotEmpty()) {
            result.append("Theory moves:\n${joinLineMoves(theoryMoves)}\n")
        }
        if (otherMoves.isNotEmpty()) {
            result.append(
                if (otherMoves.size == moveDefinitions.size) {
                    "Available moves:\n"
                } else {
                    "Other available moves:\n"
            })
            result.append(joinLineMoves(otherMoves))
            result.append("\n")
        }
        return result.toString()
    }

    private fun joinLineMoves(moveDefinitions: Collection<MoveDefinition>): String {
        return moveDefinitions.joinToString("\n") {makeMoveOptionText(it, equivalentMovesMap[it]!![0].algMove)}
    }

    private fun makeMoveOptionText(moveDefinition: MoveDefinition, algMove: String): String {
        val bookToLineMoves: MutableMap<String, MutableList<LineMove>> = mutableMapOf()
        val standaloneLineMoves: MutableList<LineMove> = mutableListOf()
        sortMoveOptions(equivalentMovesMap[moveDefinition]!!, bookToLineMoves, standaloneLineMoves)
        val result = StringBuilder()
        result.append("$algMove (")
        result.append(bookToLineMoves.entries.joinToString(", ") {
            if (it.value.size > 1 || it.value[0].chapterName == BASELINE_CHAPTER_NAME) it.key else it.value[0].fullName })
        result.append(standaloneLineMoves.joinToString(", ") { it.fullName })
        result.append(")")
        return result.toString()
    }

    fun getOpponentMove(playSettings: PlaySettings): MoveDefinition? {
        val validMoves = mutableListOf<MoveDefinition>()
        validMoves.addAll(getMovesForResult(CORRECT))
        if (validMoves.isEmpty()) validMoves.addAll(getMovesForResult(VALID))
        if (playSettings.opponentMistakes) validMoves.addAll(getMovesForResult(MISTAKE))
        return if (validMoves.isEmpty()) null else validMoves.random()
    }

    fun copy(): MoveResults {
        return MoveResults(nextMoveResults.toMap(), equivalentMovesMap.toMap(), playSettings.copy())
    }

}