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
    result += HEADER_DELIMITER
    return result
}

fun makeChapterPgn(chapter: Chapter): String {
    val currentPosition = startingPosition()
    var lineMoves = chapter.getMoves(currentPosition)
    return generateLinePgn(currentPosition, lineMoves, chapter, false)
}

fun makeMetadataToken(metadataPrefix: String, metadataValue: String): String {
    return METADATA_TOKEN_START + metadataPrefix + METADATA_VALUE_TAG + metadataValue + METADATA_VALUE_TAG + METADATA_TOKEN_END
}

private fun generateLinePgn(currentPosition: Position, lineMoves: List<LineMove>, lineTree: LineTree, isFiltered: Boolean): String {
    // Because "getMoves" will pull in moves from technically different positions (for transposition), they have to get filtered for the actual position
    // Can use an "isFiltered" flag to prevent doing this for each recursion
    val filteredMoves = if (isFiltered) lineMoves
        else lineMoves.filter { it.previousPosition == currentPosition }

    var result = ""
    for ((branchNumber, lineMove) in filteredMoves.withIndex()) {
        val turn = currentPosition.turn.toString()
        result += if (branchNumber > 0) {
            val turnToken = if (currentPosition.activeColor) "" else "$turn... "
            BRANCH_START + turnToken + generateLinePgn(currentPosition, filteredMoves.drop(branchNumber), lineTree, true) + BRANCH_END + " "
        } else {
            val turnToken = if (currentPosition.activeColor) "$turn. " else ""
            turnToken + lineMove.algMove + " " + makeMoveDetailsString(lineMove.moveDetails)
        }
    }
    if (filteredMoves.isNotEmpty()) {
        val currentMove = filteredMoves.first()
        result += generateLinePgn(currentMove.nextPosition, lineTree.getMoves(currentMove.nextPosition), lineTree, false)
    }
    return result
}

fun makeMoveDetailsString(moveDetails: MoveDetails): String {
    val commentString: String = moveDetails.description?: ""
    val tagString = moveDetails.tags.joinToString("", transform = { TAG_CHAR + it.name})
    if (commentString.isNotEmpty() || tagString.isNotEmpty()) {
        return "$COMMENT_START$commentString $tagString$COMMENT_END "
    }
    return ""
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
