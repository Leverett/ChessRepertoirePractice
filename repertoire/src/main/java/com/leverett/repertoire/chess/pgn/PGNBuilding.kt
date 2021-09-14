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

private val rulesEngine = BasicRulesEngine

fun makeLineTreeText(lineTree: LineTree): String {
    return if (lineTree is Book) makeBookText(lineTree) else makeChapterText(lineTree as Chapter)
}

fun makeBookText(book: Book): String {
    var result = if (book.description == null) "" else makeMetadataToken(BOOK_DESCRIPTION_PREFIX, book.description!!) + "\n"
    for (chapter in book.lineTrees as List<Chapter>) {
        result += makeChapterText(chapter) + CHAPTER_DELIMITER
    }
    return result
}

fun makeChapterText(chapter: Chapter): String {
    return makeChapterHeader(chapter) + makeChapterPgn(chapter)
}

fun makeChapterHeader(chapter: Chapter): String {
    val nameText = if (chapter.isStandalone()) chapter.name
        else chapter.book!!.name + BOOK_CHAPTER_NAME_DELIMITER + chapter.name
    var result = makeMetadataToken(NAME_PREFIX, nameText)
    if (chapter.description != null) {
        val descriptionToken = makeMetadataToken(CHAPTER_DESCRIPTION_PREFIX, chapter.description!!)
        result += "\n" + descriptionToken
    }
    if (chapter.startingPositionFen != null) {
        val fenToken = makeMetadataToken(FEN_PREFIX, chapter.startingPositionFen!!)
        result += "\n" + fenToken
    }
    result += HEADER_DELIMITER
    return result
}

fun makeChapterPgn(chapter: Chapter): String {
    val currentPosition = if (chapter.startingPositionFen == null) startingPosition() else positionFromFen(chapter.startingPositionFen)
    var lineMoves = chapter.getMoves(currentPosition)
    return generateLinePgn(currentPosition, lineMoves, chapter, true) + " $CHAPTER_END"
}

fun makeMetadataToken(metadataPrefix: String, metadataValue: String): String {
    return METADATA_TOKEN_START + metadataPrefix + METADATA_VALUE_TAG + metadataValue + METADATA_VALUE_TAG + METADATA_TOKEN_END
}

private fun generateLinePgn(currentPosition: Position, lineMoves: List<LineMove>, lineTree: LineTree, isFirstMove: Boolean = false): String {
    val turn = currentPosition.turn.toString()
    var result = if (isFirstMove && !currentPosition.activeColor) "$turn... " else ""
    for ((branchNumber, lineMove) in lineMoves.withIndex()) {
        result += if (branchNumber > 0) {
            TOKEN_DELIMITER.toString() + BRANCH_START + generateLinePgn(currentPosition, listOf(lineMoves[branchNumber]), lineTree, true) + BRANCH_END
        } else {
            val turnToken = if (currentPosition.activeColor) {
                "$turn. "
            } else if (lineMove.previousLineMove != null && !lineMove.previousLineMove.moveDetails.description.isNullOrBlank() && !currentPosition.activeColor && !isFirstMove) {
                "$turn... "
            } else ""
            (turnToken +
             annotatedMoveNotation(lineMove.algMove, lineMove.moveDetails) +
             TOKEN_DELIMITER +
             makeMoveDetailsString(lineMove.moveDetails)).trim()
        }
    }
    if (lineMoves.isNotEmpty()) {
        val currentMove = lineMoves.first()
        val nextPosition = currentMove.nextPosition
        val nextMoves = lineTree.getMoves(nextPosition).filter{it.previousLineMove == currentMove}
        if (nextMoves.isNotEmpty()) {
            result += TOKEN_DELIMITER + generateLinePgn(nextPosition, nextMoves, lineTree)
        }
    }
    return result
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

fun makeMoveNotation(position: Position?, move: Move?): String {
    var legalitylessMove = ""
    if (move == null || position == null) {
        return legalitylessMove
    }
    if (move == WHITE_KINGSIDE_CASTLE || move == BLACK_KINGSIDE_CASTLE) {
        legalitylessMove = KINGSIDE_CASTLE
    } else if (move == WHITE_QUEENSIDE_CASTLE || move == BLACK_QUEENSIDE_CASTLE) {
            legalitylessMove = QUEENSIDE_CASTLE
    } else {
        val startLoc = move.startLoc
        val piece = position.pieceAt(startLoc)
        val pieceType = piece.type
        if (pieceType != Piece.PieceType.PAWN) {
            legalitylessMove += pieceType.pieceTypeChar
        }

        val endLoc = move.endLoc
        val pieceRules = getPieceRules(pieceType, endLoc) as PieceRules
        if (pieceType != Piece.PieceType.PAWN) {
            legalitylessMove += disambiguatePieceToken(position, startLoc, pieceRules)
        }
        if (move.capture != Piece.EMPTY) {
            // not done in the disambiguation as it always appears in notation regardless of ambiguity
            if (pieceType == Piece.PieceType.PAWN) {
                legalitylessMove += fileToNotation(startLoc.first)
            }
            legalitylessMove += CAPTURE_CHAR
        }
        legalitylessMove += locationToNotation(endLoc)
        if (move.promotion != null) {
            legalitylessMove += (PROMOTION_CHAR.toString() + move.promotion!!.type.pieceTypeChar)
        }
    }

    return annotateLegality(legalitylessMove, rulesEngine.getNextPosition(position, move))
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
    val candidatePieceLocs = pieceRules.canMoveToCoordFrom(position.placements, position.activeColor)
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
