package com.leverett.repertoire.chess.pgn

import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.Chapter
import com.leverett.repertoire.chess.lines.LineTree
import com.leverett.repertoire.chess.move.LineMove
import com.leverett.repertoire.chess.move.MoveDetails
import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.basic.piece.PieceRules
import com.leverett.rules.chess.parsing.fileToNotation
import com.leverett.rules.chess.parsing.locationToNotation
import com.leverett.rules.chess.parsing.positionFromFen
import com.leverett.rules.chess.representation.*
import java.lang.StringBuilder

private val rulesEngine = BasicRulesEngine

fun makeLineTreeText(lineTree: LineTree): String {
    return if (lineTree is Book) makeBookText(lineTree) else makeChapterText(lineTree as Chapter)
}

fun makeBookText(book: Book, exportedChapter: Boolean = false): String {
    val result = StringBuilder()
    if (book.description != null) {
        result.append(makeMetadataToken(BOOK_DESCRIPTION_PREFIX, book.description!!))
        result.append("\n")
    }
    for (chapter in book.lineTrees) {
        result.append(makeChapterText(chapter, exportedChapter) + CHAPTER_DELIMITER)
    }
    return result.toString()
}

fun makeChapterText(chapter: Chapter, exportedChapter: Boolean = false): String {
    return makeChapterHeader(chapter) + makeChapterPgn(chapter, exportedChapter)
}

fun makeChapterHeader(chapter: Chapter): String {
    val nameText = if (chapter.isStandalone()) chapter.name
        else chapter.name
    var result = makeMetadataToken(NAME_PREFIX, nameText)
    if (chapter.description != null) {
        val descriptionToken = makeMetadataToken(CHAPTER_DESCRIPTION_PREFIX, chapter.description!!)
        result += "\n" + descriptionToken
    }
    if (chapter.startingPositionFen != null) {
        val fenToken = makeMetadataToken(FEN_PREFIX, chapter.startingPositionFen)
        result += "\n" + fenToken
    }
    result += HEADER_DELIMITER
    return result
}

/**
 * The exported chapter param is because when I reconstruct the combined, single PGN from several chapters,
 * I don't have a good way to correctly assign the previousLineMove field as I was creating the line moves.
 * This field is necessary to generate normal lineTree pgns in order to prevent possible loops and stuff
 * between the branches. Fortunately, it is guaranteed to not be necessary generated pgns
 *
 * Well that was incorrect...
 */
fun makeChapterPgn(chapter: Chapter, exportedChapter: Boolean = false): String {
    val currentPosition = if (chapter.startingPositionFen == null) startingPosition() else positionFromFen(chapter.startingPositionFen)
    val lineMoves = chapter.getMoves(currentPosition)
    val visitedPositions: MutableSet<Position> = mutableSetOf()
    return generateLinePgn(currentPosition, lineMoves, chapter, visitedPositions,true, exportedChapter) + " $CHAPTER_END"
}

fun makeMetadataToken(metadataPrefix: String, metadataValue: String): String {
    return METADATA_TOKEN_START + metadataPrefix + METADATA_VALUE_TAG + metadataValue + METADATA_VALUE_TAG + METADATA_TOKEN_END
}

private fun generateLinePgn(currentPosition: Position, lineMoves: List<LineMove>, lineTree: LineTree, visitedPositions: MutableSet<Position>, isFirstMove: Boolean = false, exportedChapter: Boolean = false): String {
    val turn = currentPosition.turn.toString()
    val result = StringBuilder()
    result.append(if (isFirstMove && !currentPosition.activeColor) "$turn... " else "")
    for ((branchNumber, lineMove) in lineMoves.withIndex()) {
        if (branchNumber > 0) {
            result.append(TOKEN_DELIMITER)
            result.append(BRANCH_START)
            result.append(generateLinePgn(currentPosition, listOf(lineMoves[branchNumber]), lineTree, visitedPositions, true, exportedChapter))
            result.append(BRANCH_END)
        } else {
            val turnToken =
                if (currentPosition.activeColor) {
                    "$turn. "
                } else if (lineMove.previousLineMove != null && !lineMove.previousLineMove.moveDetails.description.isNullOrBlank() && !currentPosition.activeColor && !isFirstMove) {
                    "$turn... "
                } else ""
            result.append(turnToken)
            result.append(annotatedMoveNotation(lineMove.algMove, lineMove.moveDetails) )
            result.append(TOKEN_DELIMITER)
            result.append(makeMoveDetailsString(lineMove.moveDetails)).trim()
        }
    }
    if (lineMoves.isNotEmpty()) {
        val currentMove = lineMoves.first()
        val nextPosition = currentMove.nextPosition
        val nextMoves = lineTree.getMoves(nextPosition).filter{it.previousLineMove == currentMove || exportedChapter}
        if (nextMoves.isNotEmpty() && !visitedPositions.contains(currentPosition)) {
            result.append(TOKEN_DELIMITER)
            result.append(generateLinePgn(nextPosition, nextMoves, lineTree, visitedPositions, exportedChapter = exportedChapter))
        }
    }
    visitedPositions.add(currentPosition)
    return result.toString()
}

fun makeMoveDetailsString(moveDetails: MoveDetails): String {
    val commentString: String = moveDetails.description?: ""
    val tagString = moveDetails.tags.mapNotNull{tagToAnnotation(it)}.joinToString("") {it + TOKEN_DELIMITER}
    return if (commentString.isNotEmpty()) {
        "$tagString$COMMENT_START $commentString $COMMENT_END "
    } else tagString
}

private fun tagToAnnotation(tag: MoveDetails.Tag): String? {
    return when (tag) {
        MoveDetails.Tag.PREFERRED -> WHITE_INITIATIVE_ANNOTATION
        MoveDetails.Tag.GAMBIT -> WHITE_ATTACK_ANNOTATION
        else -> null
    }
}

fun makeMoveNotation(position: Position?, moveAction: MoveAction?): String {
    var legalitylessMove = ""
    if (moveAction == null || position == null) {
        return legalitylessMove
    }
    if (moveAction == WHITE_KINGSIDE_CASTLE || moveAction == BLACK_KINGSIDE_CASTLE) {
        legalitylessMove = KINGSIDE_CASTLE
    } else if (moveAction == WHITE_QUEENSIDE_CASTLE || moveAction == BLACK_QUEENSIDE_CASTLE) {
            legalitylessMove = QUEENSIDE_CASTLE
    } else {
        val startLoc = moveAction.startLoc
        val piece = position.pieceAt(startLoc)
        val pieceType = piece.type
        if (pieceType != Piece.PieceType.PAWN) {
            legalitylessMove += pieceType.pieceTypeChar
        }

        val endLoc = moveAction.endLoc
        val pieceRules = getPieceRules(pieceType, endLoc) as PieceRules
        if (pieceType != Piece.PieceType.PAWN) {
            legalitylessMove += disambiguatePieceToken(position, startLoc, pieceRules)
        }
        if (moveAction.capture != Piece.EMPTY) {
            // not done in the disambiguation as it always appears in notation regardless of ambiguity
            if (pieceType == Piece.PieceType.PAWN) {
                legalitylessMove += fileToNotation(startLoc.first)
            }
            legalitylessMove += CAPTURE_CHAR
        }
        legalitylessMove += locationToNotation(endLoc)
        if (moveAction.promotion != null) {
            legalitylessMove += (PROMOTION_CHAR.toString() + moveAction.promotion!!.type.pieceTypeChar)
        }
    }

    return annotateLegality(legalitylessMove, rulesEngine.getNextPosition(position, moveAction))
}

fun annotatedMoveNotation(algMove: String, moveDetails: MoveDetails): String {
    return when {
        moveDetails.best -> algMove + BRILLIANT_ANNOTATION
        moveDetails.theory -> algMove + GOOD_ANNOTATION
        moveDetails.mistake -> algMove + MISTAKE_ANNOTATION
        else -> algMove
    }
}

private fun disambiguatePieceToken(position: Position, startLoc: Pair<Int,Int>, pieceRules: PieceRules): String {
    val candidatePieceLocs = pieceRules.canMoveToCoordFrom(position, position.activeColor)
    if (candidatePieceLocs.size == 1) {
        return ""
    }
    val candidatesFilteredByFile = candidatePieceLocs.filter { it.first == startLoc.first }
    if (candidatesFilteredByFile.size == 1) {
        return fileToNotation(startLoc.first).toString()
    }
    val candidatesFilteredByRank = candidatePieceLocs.filter { it.second == startLoc.second }
    if (candidatesFilteredByRank.size == 1) {
        return (startLoc.second + 1).toString()
    }
    return locationToNotation(startLoc)
}


private fun annotateLegality(moveToken: String, position: Position): String {
    val positionStatus = rulesEngine.positionStatus(position)
    return when {
        positionStatus.inCheck -> moveToken + CHECK_CHAR
        positionStatus.inCheckmate -> moveToken + CHECKMATE_CHAR
        else -> moveToken
    }
}
