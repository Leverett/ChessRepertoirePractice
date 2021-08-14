package com.leverett.repertoire.chess

import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.Chapter
import com.leverett.repertoire.chess.lines.LineMove
import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.basic.piece.*
import com.leverett.rules.chess.parsing.fileToNotation
import com.leverett.rules.chess.parsing.notationToFile
import com.leverett.rules.chess.parsing.notationToLocation
import com.leverett.rules.chess.representation.*
import java.util.regex.Pattern
import java.util.stream.Collectors

object PGNParsing {

    private val rulesEngine = BasicRulesEngine

    private const val CHAPTER_DELIMITER = "\n\n\n"
    private const val STUDY_HEADER_DELIMITER = "\n\n"
    private const val BOOK_CHAPTER_NAME_DELIMITER = ": "
    private const val NAME_PREFIX = "Event"
    private const val CHAPTER_DESCRIPTION_PREFIX = "ChapterDescription"
    private const val BOOK_DESCRIPTION_PREFIX = "BookDescription"
    private const val METADATA_REGEX_TOKENS = "[(.*?)]"
    private const val METADATA_VALUE_REGEX = "\"(.*?)\""
    private const val MOVE_DELIMITER = ' '
    private const val COMMENT_START = "{"
    private const val COMMENT_END = "}"
    private const val BRANCH_START = "("
    private const val BRANCH_END = ")"

    // only appears in castling, and the number indicates the castling side regardless of O's vs 0's
    private const val CASTLE_CHAR = '-'
    private const val CHECK_CHAR = '+'
    private const val CHECKMATE_CHAR = '#'
    private const val PROMOTION_CHAR = '='
    private const val CAPTURE_CHAR = 'x'

    fun parseAnnotatedPgnToBook(bookPgn: String): Book {
        val chapterStrings = bookPgn.split(CHAPTER_DELIMITER)
        val chapters = chapterStrings.parallelStream().map { parseAnnotatedPgnToChapter(it) }
            .collect(Collectors.toList())
        val bookMetadata = extractLineTreeMetadata(chapterStrings[0], true)
        return Book(bookMetadata.first, chapters, bookMetadata.second)
    }

    fun parseAnnotatedPgnToChapter(chapterPgn: String): Chapter {
        val chapterTokens = chapterPgn.split(STUDY_HEADER_DELIMITER)
        if (chapterTokens.size != 2) {
            // TODO parsing exception
        }
        val chapterMetadata = extractLineTreeMetadata(chapterTokens[0], false)
        val chapter = Chapter(chapterMetadata.first, chapterMetadata.second)

        parseMoves(chapter, chapterTokens[1], startingPosition())


        return chapter
    }

    private fun extractLineTreeMetadata(chapterMetadataString: String, book: Boolean): Pair<String, String?> {
        val chapterMetadataTokens = tokenizeMetadata(chapterMetadataString)
        val nameToken: String =
            chapterMetadataTokens.stream().filter { it.startsWith(NAME_PREFIX) }.findFirst().get()
        val nameTokenValue =
            Pattern.compile(METADATA_VALUE_REGEX).matcher(nameToken).toMatchResult().group()
        val bookAndChapterNameStrings = nameTokenValue.split(BOOK_CHAPTER_NAME_DELIMITER)
        if (bookAndChapterNameStrings.size != 2) {
            // TODO parsing exception
        }
        val name = if (book) bookAndChapterNameStrings[0] else bookAndChapterNameStrings[1]
        val descriptionPrefix = if (book) BOOK_DESCRIPTION_PREFIX else CHAPTER_DESCRIPTION_PREFIX
        val description: String? =
            chapterMetadataTokens.stream().filter { it.startsWith(descriptionPrefix) }.findFirst()
                .get()
        return Pair(name, description)
    }

    private fun tokenizeMetadata(pgnMetadata: String): List<String> {
        return Pattern.compile(METADATA_REGEX_TOKENS).matcher(pgnMetadata).results()
            .map { it.group() }.collect(Collectors.toList())
    }

    private fun parseMoves(chapter: Chapter, chapterMoves: String, position: Position) {
        var currentPosition = position
        var charIndex = 0
        var latestMove: Move? = null
        var latestMoveDetails: MoveDetails? = null
        while (charIndex < chapterMoves.length) {
            val currentChar = chapterMoves[charIndex]
            // Encountered a move
            if (currentChar.isLetter()) {
                // the trailing move processing doesn't apply in the base case of this function
                if (latestMove != null) {
                    // the previously saved move now starts from a position in the past that we don't need,
                    // so we can calculate the position that move transitions to and add the transition to the tree
                    val nextPosition = rulesEngine.getNextPosition(currentPosition, latestMove)
                    val lineMove = LineMove(
                        currentPosition.copy(),
                        nextPosition.copy(),
                        latestMove,
                        latestMoveDetails
                    )
                    chapter.addMove(lineMove)
                    // we now get set to the position before the current move gets calculated, and get rid of the comments
                    currentPosition = nextPosition
                    latestMoveDetails = null
                }

                // now we calculate the move that the current token indicates
                val moveToken = chapterMoves.substring(charIndex, chapterMoves.indexOf(MOVE_DELIMITER, charIndex))
                latestMove = makeMove(currentPosition, moveToken)

                // update index
                charIndex += moveToken.length + 1
            }
            // We found a comment, so we calclulate the move details for the currently stored move.
            // But nothing else until the next move is encountered
            if (currentChar.equals(COMMENT_START)) {
                val commentBlock =
                    chapterMoves.substring(charIndex + 1, chapterMoves.indexOf(COMMENT_END, charIndex))
                latestMoveDetails = makeMoveDetails(commentBlock)
                charIndex += commentBlock.length + 1
            }
            // In this case, start a recursive process, parsing the contents of the branch and adding them to the same chapter
            // We don't do anything else though, it's as if the internal process is about to encounter a move
            if (currentChar.equals(BRANCH_START)) {
                val branchBlock =
                    chapterMoves.substring(charIndex + 1, chapterMoves.indexOf(BRANCH_END, charIndex))
                parseMoves(chapter, branchBlock, currentPosition.copy())
                charIndex += branchBlock.length + 1
            }
        }

    }

    private fun makeMove(position: Position, moveToken: String): Move {
        val activeColor = position.activeColor

        if (moveToken.contains(CASTLE_CHAR)) {
            return castleMove(activeColor, moveToken.filter { it == CASTLE_CHAR }.count() == 1)!!
        }

        var token = moveToken
        if (token.contains(CHECK_CHAR) || token.contains(CHECKMATE_CHAR)) {
            token = token.dropLast(1)
        }

        var promotionPiece: PieceEnum? = null
        if (token.contains(PROMOTION_CHAR)) {
            val promotionPieceType = getPieceType(token[token.length-1])
            promotionPiece = getPiece(activeColor, promotionPieceType)
            token = token.dropLast(3)
        }

        val endLoc = notationToLocation(token.substring(token.length - 2, token.length))
        val capture = position.pieceAt(endLoc)
        token = token.dropLast(2)

        val pieceType = if (token.first().isLowerCase()) PieceEnum.PieceType.PAWN else {
            getPieceType(token.first()).also { token = token.drop(1) }
        }
        if (token.last() == CAPTURE_CHAR) {
            token = token.dropLast(1)
        }
        val piece = getPieceRules(pieceType, endLoc) as Piece
        val threateningLocations = piece.threatensCoord(position.placements, activeColor)
        val startLoc = findStartLoc(threateningLocations, token)
        return Move(startLoc, endLoc, capture, promotionPiece)
    }

    private fun findStartLoc(threateningLocations: List<Pair<Int,Int>>, token: String): Pair<Int,Int> {
        //TODO might want to validate here, there should only be the bare necessity in the token
        if (threateningLocations.size == 1) {
            return threateningLocations[0]
        }
        if (token.isNotEmpty()) {
            return threateningLocations
                .filter { !token.first().isLetter() || it.first == notationToFile(token.first()) }
                .first { !token.last().isDigit() || it.second == notationToFile(token.last()) }

        }
        return Pair(-1, -1) //failure case
    }

    private fun getPieceRules(pieceType: PieceEnum.PieceType, endLoc: Pair<Int,Int>): Piece? {
        return when (pieceType) {
            PieceEnum.PieceType.PAWN -> Pawn(endLoc.first, endLoc.second)
            PieceEnum.PieceType.KNIGHT -> Knight(endLoc.first, endLoc.second)
            PieceEnum.PieceType.BISHOP -> Bishop(endLoc.first, endLoc.second)
            PieceEnum.PieceType.ROOK -> Rook(endLoc.first, endLoc.second)
            PieceEnum.PieceType.QUEEN -> Queen(endLoc.first, endLoc.second)
            PieceEnum.PieceType.KING -> King(endLoc.first, endLoc.second)
            else -> null
        }
    }

    private fun makeMoveDetails(commentBlock: String): MoveDetails? {
        return null
    }
}