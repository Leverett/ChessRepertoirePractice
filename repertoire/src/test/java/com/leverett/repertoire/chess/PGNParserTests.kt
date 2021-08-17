package com.leverett.repertoire.chess

import com.leverett.repertoire.chess.lines.Chapter
import com.leverett.rules.chess.representation.*
import com.leverett.rules.chess.representation.Piece.*
import org.testng.Assert.assertEquals
import org.testng.Assert.assertNotNull
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.logging.Level
import java.util.logging.Logger

class PGNParserTests {

    private val parser = PGNParser

    @DataProvider(name = "extractSingleLayerBlockData")
    fun extractSingleLayerBlockData(): Array<Array<Any>> {
        return arrayOf(
            arrayOf("[text in the block]", 0, ']', "text in the block"),
            arrayOf("not in [text in the block]", 7, ']', "text in the block"),
            arrayOf("[text in the block] afterwards", 0, ']', "text in the block"),
            arrayOf("both [text in the block] sides", 5, ']', "text in the block"),
            arrayOf("{text in the block}", 0, '}', "text in the block"),
            arrayOf("<text in the block>", 0, '>', "text in the block"),
        )
    }

    @Test(dataProvider = "extractSingleLayerBlockData")
    fun extractSingleLayerBlockTest(text: String, charIndex: Int, endChar: Char, expectedValue: String) {

        val actualValue = parser.extractSingleLayerBlock(text, charIndex, endChar)
        assertEquals(actualValue, expectedValue)
    }

    @DataProvider(name = "extractNestedBlockData")
    fun extractNestedBlockData(): Array<Array<Any>> {
        return arrayOf(
            arrayOf("(basic one layer)", 1, "basic one layer"),
            arrayOf("not in (text in the block)", 8, "text in the block"),
            arrayOf("now we (are going (deeper) but getting out) with more to spare", 8, "are going (deeper) but getting out"),
            arrayOf("just (to (push (the (limit) I )guess))   ", 6, "to (push (the (limit) I )guess)"),
        )
    }

    @Test(dataProvider = "extractNestedBlockData")
    fun extractNestedBlockTest(text: String, charIndex: Int, expectedValue: String) {

        val actualValue = parser.extractNestedBlock(text, charIndex, '(', ')')
        assertEquals(actualValue, expectedValue)
    }

    @DataProvider(name = "extractTagData")
    fun extractTagData(): Array<Array<Any?>> {
        return arrayOf(
            arrayOf("\$BEST", 0, MoveDetails.Tag.BEST),
            arrayOf(" a \$THEORY", 3, MoveDetails.Tag.THEORY),
            arrayOf("\$BEasdST", 0, null),
            arrayOf("\$BESTandmore", 0, MoveDetails.Tag.BEST),
            arrayOf("\$GAMBIT and more", 0, MoveDetails.Tag.GAMBIT),
        )
    }

    @Test(dataProvider = "extractTagData")
    fun extractTagTest(text: String, charIndex: Int, expectedValue: MoveDetails.Tag?) {

        val actualValue = parser.extractTag(text, charIndex)
        assertEquals(actualValue, expectedValue)
    }

    @DataProvider(name = "extractMoveData")
    fun extractMoveData(): Array<Array<Any?>> {
        return arrayOf(
            arrayOf("Rh2 ", 0, "Rh2"),
            arrayOf("  Ngxe6#  ", 2, "Ngxe6#"),
        )
    }

    @Test(dataProvider = "extractMoveData")
    fun extractMoveTest(text: String, charIndex: Int, expectedValue: String) {

        val actualValue = parser.extractMove(text, charIndex)
        assertEquals(actualValue, expectedValue)
    }

    @DataProvider(name = "parseCommentBlockData")
    fun parseCommentBlockData(): Array<Array<Any?>> {
        return arrayOf(
            arrayOf("some basic comment", "some basic comment", false, false),
            arrayOf("  some basic comment with whitespace ", "some basic comment with whitespace", false, false),
            arrayOf("  tagged comment \$BEST with best ", "tagged comment  with best", true, false),
            arrayOf("\$THEORY tagged at the start", "tagged at the start", false, true),
            arrayOf(" tagged at the end \$BEST", "tagged at the end", true, false),
            arrayOf("\$THEORY tagged on both sides \$BEST", "tagged on both sides", true, true),
            arrayOf("\$THEORY\$BEST two tags no space", "two tags no space", true, true),
            arrayOf("\$THEORY\$BEST  ", null, true, true),
            arrayOf("middle \$BEST tag", "middle  tag", true, false),
            arrayOf("  ", null, false, false),
        )
    }

    @Test(dataProvider = "parseCommentBlockData")
    fun parseCommentBlockTest(text: String, expectedDescription: String?, expectedBestMove: Boolean, expectedTheory: Boolean) {

        val actualValue = MoveDetails()
        parser.parseCommentBlock(text, actualValue)

        assertEquals(actualValue.description, expectedDescription)
        assertEquals(actualValue.best, expectedBestMove)
        assertEquals(actualValue.theory, expectedTheory)
    }

    @DataProvider(name = "findStartLocData")
    fun findStartLocData(): Array<Array<Any?>> {
        return arrayOf(
            arrayOf(listOf(Pair(2,4)), "c5", Pair(2,4)),
            arrayOf(listOf(Pair(0,4),Pair(2,5)), "a", Pair(0,4)),
            arrayOf(listOf(Pair(2,5),Pair(0,4)), "a", Pair(0,4)),
            arrayOf(listOf(Pair(0,3),Pair(2,5)), "6", Pair(2,5)),
            arrayOf(listOf(Pair(0,5),Pair(0,3)), "4", Pair(0,3)),
            arrayOf(listOf(Pair(0, 4), Pair(3, 0), Pair(0,0)), "a1", Pair(0,0)),
        )
    }

    @Test(dataProvider = "findStartLocData")
    fun findStartLocTest(threateningLocations: List<Pair<Int,Int>>, token: String, expectedValue: Pair<Int,Int>) {

        val actualValue = parser.findStartLoc(threateningLocations, token)

        assertEquals(actualValue, expectedValue)
    }

    @DataProvider(name = "makeMoveData")
    fun makeMoveData(): Array<Array<Any?>> {
        return arrayOf(
            // regular piece moves from each color
            arrayOf("Rh2", true, Move(Pair(7,0), Pair(7,1), EMPTY)),
            arrayOf("Rc8+", false, Move(Pair(0,7), Pair(2,7), EMPTY)),
            // pawn moves
            arrayOf("b6+", false, Move(Pair(1,6), Pair(1,5), EMPTY)),
            arrayOf("b5+", false, Move(Pair(1,6), Pair(1,4), EMPTY)),
            // regular moves for duplicate pieces on the same file
            arrayOf("Q2b3", true, Move(Pair(1,1), Pair(1,2), EMPTY)),
            arrayOf("Q4b3", true, Move(Pair(1,3), Pair(1,2), EMPTY)),
            // capture moves for duplicate pieces on the same file
            arrayOf("R5xa7#", false, Move(Pair(0,4), Pair(0,6), WHITE_BISHOP)),
            arrayOf("R8xa7", false, Move(Pair(0,7), Pair(0,6), WHITE_BISHOP)),
            // regular moves for duplicate pieces on the same rank
            arrayOf("Nce4+", false, Move(Pair(2,4), Pair(4,3), EMPTY)),
            arrayOf("Nge4", false, Move(Pair(6,4), Pair(4,3), EMPTY)),
            // capture moves for duplicate pieces on the same rank
            arrayOf("Ncxe6", false, Move(Pair(2,4), Pair(4,5), WHITE_PAWN)),
            arrayOf("Ngxe6#", false, Move(Pair(6,4), Pair(4,5), WHITE_PAWN)),
            // moves for duplicate pieces on the same rank and file
            arrayOf("Qb4d2", true, Move(Pair(1,3), Pair(3, 1), EMPTY)),
            arrayOf("Qb2xd4+", true, Move(Pair(1,1), Pair(3, 3), BLACK_PAWN)),
            // promotions
            arrayOf("h8=Q", true, Move(Pair(7,6), Pair(7, 7), EMPTY, promotion = WHITE_QUEEN)),
            arrayOf("hxg8=Q", true, Move(Pair(7,6), Pair(6, 7), BLACK_BISHOP, promotion = WHITE_QUEEN)),
            // enpassant
            arrayOf("hxg3", false, Move(Pair(7,3), Pair(6, 2), WHITE_PAWN, enPassant = true)),
            // castling
            arrayOf("O-O", true, WHITE_KINGSIDE_CASTLE),
            arrayOf("O-O", false, BLACK_KINGSIDE_CASTLE),
            arrayOf("0-0", true, WHITE_KINGSIDE_CASTLE),
            arrayOf("0-0+", false, BLACK_KINGSIDE_CASTLE),
            arrayOf("O-O-O", true, WHITE_QUEENSIDE_CASTLE),
            arrayOf("O-O-O#", false, BLACK_QUEENSIDE_CASTLE),
            arrayOf("0-0-0", true, WHITE_QUEENSIDE_CASTLE),
            arrayOf("0-0-0", false, BLACK_QUEENSIDE_CASTLE),

            )
    }

    @Test(dataProvider = "makeMoveData")
    fun makeMoveTest(token: String, activeColor: Boolean, expectedValue: Move) {

        val castling = Castling( whiteKingside = true,whiteQueenside = true,blackKingside = true,blackQueenside = true)
        val enPassantTarget = if (!activeColor) Pair(6, 2) else null
        val position = Position(testingPlacements, activeColor, castling, enPassantTarget, 0)

        val actualValue = parser.makeMove(position, token)

        assertEquals(actualValue, expectedValue)
    }

    @Test
    fun parseMovesTest() {

        val startingPosition = startingPosition()
        val chapter = Chapter("test")
        parser.parseMoves(chapter, CHAPTER_MOVES_EXAMPLE, startingPosition)

        assertNotNull(chapter.getMoves(startingPosition))
        var position = startingPosition
        while (true) {
            val moves = chapter.getMoves(position)
            if (moves.isNotEmpty()) {
                position = moves[0].nextPosition
                Logger.getLogger("parseMovesTest").log(Level.SEVERE, position.quickDisplay())
            } else {
                break
            }
        }
    }

    private val testingPlacements: Array<Array<Piece>> = arrayOf(
        arrayOf(EMPTY     , EMPTY      , EMPTY, EMPTY      , BLACK_ROOK  , EMPTY     , WHITE_BISHOP, BLACK_ROOK),
        arrayOf(EMPTY     , WHITE_QUEEN, EMPTY, WHITE_QUEEN, EMPTY       , EMPTY     , BLACK_PAWN  , EMPTY),
        arrayOf(EMPTY     , EMPTY      , EMPTY, EMPTY      , BLACK_KNIGHT, EMPTY     , EMPTY       , EMPTY),
        arrayOf(EMPTY     , EMPTY      , EMPTY, BLACK_PAWN , EMPTY       , BLACK_PAWN, EMPTY       , EMPTY),
        arrayOf(WHITE_KING, EMPTY      , EMPTY, EMPTY      , EMPTY       , WHITE_PAWN, EMPTY       , EMPTY),
        arrayOf(EMPTY     , EMPTY      , EMPTY, WHITE_QUEEN, EMPTY       , EMPTY     , BLACK_BISHOP, EMPTY),
        arrayOf(EMPTY     , EMPTY      , EMPTY, WHITE_PAWN , BLACK_KNIGHT, WHITE_PAWN, EMPTY       , BLACK_BISHOP),
        arrayOf(WHITE_ROOK, EMPTY      , EMPTY, BLACK_PAWN , BLACK_KING  , EMPTY     , WHITE_PAWN  , EMPTY),
    )


}
private const val CHAPTER_MOVES_EXAMPLE = "1. d4 d5 2. c4 dxc4 { hacking tags <GAMBIT,BEST,THEORY> maybe there can be multiple <OTHER> } 3. e4 { Comment on third move } (3. e3 b5 (3... e5 4. dxe5 \$22)  (3... f5) 4. Qf3 \$140 (4. Nc3 e6)) 3... c5 4. e5 f6 { This is a random comment\n" +
        "With new lines\n" +
        "a lot of them } (4... g6) 5. f3 g6 (5... h5) 6. a4 *"