package com.leverett.repertoire.chess.pgn

import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.Chapter
import com.leverett.repertoire.chess.move.LineMove
import com.leverett.repertoire.chess.move.MoveDefinition
import com.leverett.repertoire.chess.move.MoveDetails
import com.leverett.repertoire.chess.settings.Configuration
import com.leverett.rules.chess.representation.Position
import com.leverett.rules.chess.representation.startingPosition
import java.lang.StringBuilder
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue


fun makeRepertoirePgnForConfiguration(configuration: Configuration): String {
    val chapter = makeRepertoireChapterForConfiguration(configuration)
    return makeChapterPgn(chapter, true)
}

private fun makeRepertoireChapterForConfiguration(configuration: Configuration): Chapter {
    val result = Chapter(configuration.name)
    val chapters = getActiveChapters(configuration.activeRepertoire)
    val equivalentMovesMap: MutableMap<MoveDefinition, MutableList<LineMove>> = mutableMapOf()

    val positions: Queue<Position> = ConcurrentLinkedQueue<Position>().also{it.add(startingPosition())}
    val visitedPositions: MutableSet<String> = mutableSetOf()
    var position: Position? = startingPosition()
    var isRepertoireColor = configuration.color
    while (position != null) {
        val lineMoves: List<LineMove> = if (isRepertoireColor) {
            chapters.map { it.getPreferredMoves(position!!) }.flatten()
        } else {
            chapters.map { it.getMoves(position!!) }.flatten()
        }
        lineMoves.forEach{
            equivalentMovesMap.putIfAbsent(it.moveDefinition, mutableListOf())
            equivalentMovesMap[it.moveDefinition]!!.add(it)
            val nextPosition = it.nextPosition
            if (!visitedPositions.contains(nextPosition.statelessPositionHash)) {
                positions.add(nextPosition)
            }
            visitedPositions.add(nextPosition.statelessPositionHash)
        }
        isRepertoireColor = !isRepertoireColor
        position = positions.poll()
    }

    equivalentMovesMap.entries.stream()
        .map { LineMove(it.key, getMoveDetails(it.value), null, configuration.name, null, null) }
        .forEach{result.addMove(it)}

    return result
}

private fun getMoveDetails(lineMoves: List<LineMove>): MoveDetails {
    val result = MoveDetails(getMoveDescriptionText(lineMoves))
    if (lineMoves.any { it.mistake }) {
        result.addTag(MoveDetails.Tag.MISTAKE)
    }
    return result
}

private fun getActiveChapters(activeRepertoire: MutableSet<String>): Set<Chapter> {
    val result: MutableSet<Chapter> = mutableSetOf()
    for (lineTree in RepertoireManager.repertoire.lineTrees) {
        if (lineTree is Chapter && activeRepertoire.contains(lineTree.name)) {
            result.add(lineTree)
        } else {
            val book = lineTree as Book
            if (activeRepertoire.contains(book.name)) {
                result.addAll(lineTree.chapters)
            } else {
                result.addAll(book.chapters.filter { activeRepertoire.contains(it.name) })
            }
        }
    }
    return result
}
fun getMoveDescriptionText(lineMoves: List<LineMove>): String {
    val bookToLineMoves: MutableMap<String, MutableList<LineMove>> = mutableMapOf()
    val standaloneLineMoves: MutableList<LineMove> = mutableListOf()
    sortMoveOptions(lineMoves, bookToLineMoves, standaloneLineMoves)

    val result = StringBuilder()
    result.append(bookToLineMoves.entries.map{ getMoveDescriptionsTextForBook(it.key, it.value) }.filter{ it.isNotBlank() }.joinToString(" - ") )
    if (standaloneLineMoves.isNotEmpty()) {
        result.append(" --- ")
        result.append(standaloneLineMoves.joinToString(" - ") {
            if (it.moveDetails.description.isNullOrBlank()) it.fullName
            else "${it.fullName}: ${it.moveDetails.description}"})
    }
    return result.toString()
}

private fun getMoveDescriptionsTextForBook(bookName: String, lineMoves: List<LineMove>): String {
    if (lineMoves.size == 1) {
        val lineMove = lineMoves[0]
        val description = lineMove.moveDetails.description
        if (!description.isNullOrBlank()) {
            return "${lineMove.fullName} - ${description.trim()}"
        }
    }
    val descriptionsToChapters = mutableMapOf<String, MutableList<String>>()
    lineMoves.forEach{
        val description = it.moveDetails.description?.trim()
        if (!description.isNullOrBlank()) {
            val chapters = descriptionsToChapters[description]
            if (chapters != null) {
                chapters.add(it.chapterName)
            } else {
                descriptionsToChapters[description] = mutableListOf(it.chapterName)
            }
        }
    }
    if (descriptionsToChapters.size == 1) {
        val description = descriptionsToChapters.keys.first()
        val chapterName = if (descriptionsToChapters.values.first().size == 1) {"${descriptionsToChapters.values.first().first()}: "} else ""
        return "$bookName: $chapterName$description"
    }
    if (descriptionsToChapters.size > 1) {
        return descriptionsToChapters.keys.joinToString(" - ")
    }
    return ""
}


fun sortMoveOptions(lineMoves: List<LineMove>, bookToLineMoves: MutableMap<String, MutableList<LineMove>>, standaloneLineMoves: MutableList<LineMove>) {
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

