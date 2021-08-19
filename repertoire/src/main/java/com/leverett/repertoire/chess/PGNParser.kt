package com.leverett.repertoire.chess

import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.Chapter
import com.leverett.repertoire.chess.lines.LineMove
import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.basic.piece.*
import com.leverett.rules.chess.parsing.notationToFile
import com.leverett.rules.chess.parsing.notationToLocation
import com.leverett.rules.chess.representation.*
import org.apache.commons.lang3.StringUtils
import java.util.stream.Collectors

object PGNParser {

    private val rulesEngine = BasicRulesEngine

    private const val CHAPTER_DELIMITER = "\n\n\n"
    private const val STUDY_HEADER_DELIMITER = "\n\n"
    private const val BOOK_CHAPTER_NAME_DELIMITER = ": "
    private const val NAME_PREFIX = "Event"
    private const val CHAPTER_DESCRIPTION_PREFIX = "ChapterDescription"
    private const val BOOK_DESCRIPTION_PREFIX = "BookDescription"
    private const val METADATA_TOKEN_START = "["
    private const val METADATA_TOKEN_END = "]"
    private const val METADATA_VALUE_TAG = "\""
    private const val MOVE_DELIMITER = ' '
    private const val COMMENT_START = '{'
    private const val COMMENT_END = '}'
    private const val BRANCH_START = '('
    private const val BRANCH_END = ')'
    private const val GRAPHIC_START = '['
    private const val GRAPHIC_END = ']'
    private const val TAG_START = '$'

    // only appears in castling, and the number indicates the castling side regardless of O's vs 0's
    private const val CASTLE_CHAR = '-'
    private const val CHECK_CHAR = '+'
    private const val CHECKMATE_CHAR = '#'
    private const val PROMOTION_CHAR = '='
    private const val CAPTURE_CHAR = 'x'
    private const val CHAPTER_END = '*'

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

    internal fun extractLineTreeMetadata(chapterMetadataString: String, book: Boolean): Pair<String, String?> {
        val chapterMetadataTokens = StringUtils.substringsBetween(chapterMetadataString, METADATA_TOKEN_START, METADATA_TOKEN_END).toList()
        val nameToken: String =
            chapterMetadataTokens.stream().filter { it.startsWith(NAME_PREFIX) }.findFirst().get()
        val nameTokenValue = StringUtils.substringBetween(nameToken, METADATA_VALUE_TAG)
        val bookAndChapterNameStrings = nameTokenValue.split(BOOK_CHAPTER_NAME_DELIMITER)
        if (bookAndChapterNameStrings.size != 2) {
            // TODO parsing exception
        }
        val name = if (book) bookAndChapterNameStrings[0] else bookAndChapterNameStrings[1]
        val descriptionPrefix = if (book) BOOK_DESCRIPTION_PREFIX else CHAPTER_DESCRIPTION_PREFIX
        val descriptionToken =
            chapterMetadataTokens.stream().filter { it.startsWith(descriptionPrefix) }.findFirst()
        val description = if(descriptionToken.isPresent) StringUtils.substringBetween(descriptionToken.get(), METADATA_VALUE_TAG) else null
        return Pair(name, description)
    }

    internal fun parseMoves(chapter: Chapter, chapterMoves: String, position: Position) {
        var currentPosition = position
        var latestMove: Move? = null
        var latestMoveDetails = MoveDetails()
        var latestMoveToken = ""
        var charIndex = 0
        while (charIndex < chapterMoves.length) {
            val currentChar = chapterMoves[charIndex]
            when {
                // Encountered a move
                currentChar.isLetter() -> {
                    // the trailing move processing doesn't apply in the base case of this function
                    if (latestMove != null) {
                        // the previously saved move now starts from a position in the past that we don't need,
                        // so we can calculate the position that move transitions to and add the transition to the tree
                        val nextPosition = rulesEngine.getNextPosition(currentPosition, latestMove)
                        val lineMove = LineMove(
                            chapter,
                            currentPosition.copy(),
                            nextPosition.copy(),
                            latestMove.copy(),
                            latestMoveDetails.copy(),
                            latestMoveToken
                        )
                        chapter.addMove(lineMove)
                        // we now get set to the position before the current move gets calculated, and get rid of the comments
                        currentPosition = nextPosition
                        latestMoveDetails = MoveDetails()
                    }

                    // now we calculate the move that the current token indicates
                    latestMoveToken = extractMove(chapterMoves, charIndex)
                    latestMove = makeMove(currentPosition, latestMoveToken)

                    // update index
                    charIndex += latestMoveToken.length + 1
                }
                // We found a comment, so we calculate the move details for the currently stored move.
                // But nothing else until the next move is encountered
                currentChar == COMMENT_START -> {
                    val commentBlock = extractSingleLayerBlock(chapterMoves, charIndex, COMMENT_END)
                    parseCommentBlock(commentBlock, latestMoveDetails)
                    charIndex += commentBlock.length + 2
                }
                // In this case, start a recursive process, parsing the contents of the branch and adding them to the same chapter
                // We don't do anything else though, it's as if the internal process is about to encounter a move
                currentChar == BRANCH_START -> {
                    val branchBlock = extractNestedBlock(chapterMoves, charIndex + 1, BRANCH_START, BRANCH_END)
                    parseMoves(chapter, branchBlock, currentPosition.copy())
                    charIndex += branchBlock.length + 2
                }
                // Just add a tag for the currently stored move
                currentChar == TAG_START -> {
                    val tag = extractTag(chapterMoves, charIndex)
                    if (tag != null) {
                        latestMoveDetails.addTag(tag)
                        charIndex += tag.name.length
                    }
                    charIndex++
                }
                currentChar == CHAPTER_END -> {
                    break
                }
                else -> charIndex++
            }
        }
        val nextPosition = rulesEngine.getNextPosition(currentPosition, latestMove!!)
        val lineMove = LineMove(
            chapter,
            currentPosition.copy(),
            nextPosition.copy(),
            latestMove,
            latestMoveDetails.copy(),
            latestMoveToken
        )
        chapter.addMove(lineMove)
    }

    internal fun makeMove(position: Position, moveToken: String): Move {
        val activeColor = position.activeColor

        if (moveToken.contains(CASTLE_CHAR)) {
            return castleMove(activeColor, moveToken.filter { it == CASTLE_CHAR }.count() == 1)!!
        }

        var token = moveToken
        if (token.contains(CHECK_CHAR) || token.contains(CHECKMATE_CHAR)) {
            token = token.dropLast(1)
        }

        var promotionPiece: Piece? = null
        if (token.contains(PROMOTION_CHAR)) {
            val promotionPieceType = getPieceType(token[token.length-1])
            promotionPiece = getPiece(activeColor, promotionPieceType)
            token = token.dropLast(2)
        }

        val endLoc = notationToLocation(token.substring(token.length - 2, token.length))
        token = token.dropLast(2)

        val pieceType = if (token.isEmpty() || token.first().isLowerCase()) Piece.PieceType.PAWN else {
            getPieceType(token.first()).also { token = token.drop(1) }
        }
        if (token.isNotEmpty() && token.last() == CAPTURE_CHAR) {
            token = token.dropLast(1)
        }
        val piece = getPieceRules(pieceType, endLoc) as PieceRules
        val accessibleLocations = piece.canMoveToCoordFrom(position.placements, activeColor, position.enPassantTarget)
        val startLoc = findStartLoc(accessibleLocations, token)
        val enPassant = (endLoc == position.enPassantTarget && pieceType == Piece.PieceType.PAWN)
        val capture = if (enPassant) getPiece(!activeColor, Piece.PieceType.PAWN) else position.pieceAt(endLoc)
        return Move(startLoc, endLoc, capture, promotionPiece, enPassant)
    }

    internal fun findStartLoc(accessibleLocations: List<Pair<Int,Int>>, token: String): Pair<Int,Int> {
        if (accessibleLocations.size == 1) {
            return accessibleLocations[0]
        }
        if (token.isNotEmpty()) {
            return accessibleLocations
                .filter { !token.first().isLetter() || it.first == notationToFile(token.first()) }
                .first { !token.last().isDigit() || it.second == token.last().digitToInt() - 1 }

        }
        return Pair(-1, -1) //failure case
    }

    internal fun parseCommentBlock(commentBlock: String, moveDetails: MoveDetails) {
        var charIndex = 0
        var descripton = ""
        while (charIndex < commentBlock.length) {
            val currentChar = commentBlock[charIndex]
            when (currentChar) {
                GRAPHIC_START -> {
                    charIndex += extractSingleLayerBlock(commentBlock, charIndex, GRAPHIC_END).length + 2
                }
                TAG_START -> {
                    val tag = extractTag(commentBlock, charIndex)
                    if (tag != null) {
                        moveDetails.addTag(tag)
                        charIndex += tag.name.length
                    }
                    charIndex++
                }
                else -> {
                    descripton += currentChar
                    charIndex++
                }
            }
        }
        descripton = descripton.trim()
        if (descripton.isNotEmpty()) {
            moveDetails.description = descripton
        }
    }

    internal fun extractSingleLayerBlock(text: String, charIndex: Int, endChar: Char): String {
        val endCharIndex = text.indexOf(endChar, charIndex)
        val endIndex = if (endCharIndex == -1) text.length else endCharIndex
        return text.substring(charIndex + 1, endIndex)
    }

    internal fun extractNestedBlock(text: String, startIndex: Int, startChar: Char, endChar: Char): String {
        var result = ""
        var layers = 1
        var charIndex = startIndex
        do {
            val currentChar = text[charIndex]
            result += currentChar
            charIndex ++
            when (text[charIndex]) {
                startChar -> layers++
                endChar -> layers--
            }
        } while (layers > 0)
        return result
    }

    internal fun extractMove(text: String, charIndex: Int): String {
        val endCharIndex = text.indexOf(MOVE_DELIMITER, charIndex)
        val endIndex = if (endCharIndex == -1) text.length else endCharIndex
        return text.substring(charIndex, endIndex)
    }

    internal fun extractTag(text: String, charIndex: Int): MoveDetails.Tag? {
        for (tag in MoveDetails.Tag.values()) {
            if (text.substring(charIndex+1).startsWith(tag.name)) {
                return tag
            }
        }
        return null
    }
}