package com.leverett.repertoire.chess.pgn

import com.leverett.repertoire.chess.lines.Book
import com.leverett.rules.chess.representation.*
import com.leverett.rules.chess.representation.Piece.*
import org.testng.Assert.assertEquals
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class PGNBuildingTests {

    private val bookExample = "[Event \"Test Study: Chapter 1\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/32qGstHX\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:15:10\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D20\"]\n" +
            "[Opening \"Queen's Gambit Accepted: Old Variation\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. d4 \$THEORY {just a comment} d5 {comment for a hint} 2. c4 dxc4 \$MISTAKE 3. e3 (3. e4 \$MISTAKE) 3... b5 4. Qf3 *\n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 2\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/C8nP4WlO\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:33:53\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D30\"]\n" +
            "[Opening \"Queen's Gambit Declined\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. d4 d5 2. c4 e6 *\n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 3\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/C8nP4WlO\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:33:53\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D30\"]\n" +
            "[Opening \"Queen's Gambit Declined\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. e4 \$THEORY d5 2. c4 e6 *"
    private val book: Book = parseAnnotatedPgnToBook(bookExample)

    private val bookResult = "[Event \"Test Study: Chapter 1\"]\n" +
            "\n" +
            "1. d4 {just a comment \$THEORY} d5 {comment for a hint } 2. c4 dxc4 { \$MISTAKE} 3. e3 (3. e4 { \$MISTAKE} ) b5 4. Qf3 \n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 2\"]\n" +
            "\n" +
            "1. d4 d5 2. c4 e6 \n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 3\"]\n" +
            "\n" +
            "1. e4 { \$THEORY} d5 2. c4 e6 \n\n\n"

    private val bookExample2 = "[Event \"Test Study: Chapter 1\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/32qGstHX\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:15:10\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D20\"]\n" +
            "[Opening \"Queen's Gambit Accepted: Old Variation\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. d4 \$THEORY\$PREFERRED\$GAMBIT {just a comment} d5 {comment for a hint} 2. c4 dxc4 \$BEST 3. e3 (3. e4 \$MISTAKE) 3... b5 4. Qf3 *\n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 2\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/C8nP4WlO\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:33:53\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D30\"]\n" +
            "[Opening \"Queen's Gambit Declined\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. d4 d5 { \$BEST } 2. c4 { \$GAMBIT } e6 *\n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 3\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/C8nP4WlO\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:33:53\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D30\"]\n" +
            "[Opening \"Queen's Gambit Declined\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. e4 \$THEORY d5 2. c4 e6 *"
    private val book2: Book = parseAnnotatedPgnToBook(bookExample2)

    private val bookResult2 = "[Event \"Test Study: Chapter 1\"]\n" +
            "\n" +
            "1. d4! $WHITE_INITIATIVE_ANNOTATION $WHITE_ATTACK_ANNOTATION { just a comment } d5 { comment for a hint } 2. c4 dxc4!! 3. e3 (3. e4?) b5 4. Qf3 *\n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 2\"]\n" +
            "\n" +
            "1. d4 d5!! 2. c4 $WHITE_ATTACK_ANNOTATION e6 *\n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 3\"]\n" +
            "\n" +
            "1. e4! d5 2. c4 e6 *\n\n\n"


    @Test
    fun testTest() {
//        val pgn = makeBookText(book)
//        assertEquals(pgn, bookResult)
        val pgn = makeBookText(book2)
        assertEquals(pgn, bookResult2)
    }



    @DataProvider(name = "makeMoveNotationData")
    fun makeMoveNotationData(): Array<Array<Any?>> {
        return arrayOf(
            // regular piece moves from each color
            arrayOf("Rh2", true, MoveAction(Pair(7,0), Pair(7,1), EMPTY)),
            arrayOf("Rc8", false, MoveAction(Pair(0,7), Pair(2,7), EMPTY)),
            // pawn moves
            arrayOf("b6", false, MoveAction(Pair(1,6), Pair(1,5), EMPTY)),
            arrayOf("b5", false, MoveAction(Pair(1,6), Pair(1,4), EMPTY)),
            // regular moves for duplicate pieces on the same file
            arrayOf("Q2b3", true, MoveAction(Pair(1,1), Pair(1,2), EMPTY)),
            arrayOf("Q4b3", true, MoveAction(Pair(1,3), Pair(1,2), EMPTY)),
            // capture moves for duplicate pieces on the same file
            arrayOf("R5xa7", false, MoveAction(Pair(0,4), Pair(0,6), WHITE_BISHOP)),
            arrayOf("R8xa7", false, MoveAction(Pair(0,7), Pair(0,6), WHITE_BISHOP)),
            // regular moves for duplicate pieces on the same rank
            arrayOf("Nce4", false, MoveAction(Pair(2,4), Pair(4,3), EMPTY)),
            arrayOf("Nge4", false, MoveAction(Pair(6,4), Pair(4,3), EMPTY)),
            // capture moves for duplicate pieces on the same rank
            arrayOf("Ncxe6", false, MoveAction(Pair(2,4), Pair(4,5), WHITE_PAWN)),
            arrayOf("Ngxe6", false, MoveAction(Pair(6,4), Pair(4,5), WHITE_PAWN)),
            // moves for duplicate pieces on the same rank and file
            arrayOf("Qb4d2", true, MoveAction(Pair(1,3), Pair(3, 1), EMPTY)),
            // promotions
            arrayOf("h8=Q+", true, MoveAction(Pair(7,6), Pair(7, 7), EMPTY, promotion = WHITE_QUEEN)),
            arrayOf("hxg8=Q", true, MoveAction(Pair(7,6), Pair(6, 7), BLACK_BISHOP, promotion = WHITE_QUEEN)),
            // enpassant
            arrayOf("hxg3", false, MoveAction(Pair(7,3), Pair(6, 2), WHITE_PAWN, enPassant = true)),
            // castling
            arrayOf("O-O", true, WHITE_KINGSIDE_CASTLE),
            arrayOf("O-O", false, BLACK_KINGSIDE_CASTLE),
            arrayOf("O-O-O", true, WHITE_QUEENSIDE_CASTLE),
            arrayOf("O-O-O", false, BLACK_QUEENSIDE_CASTLE),

            )
    }

    @Test(dataProvider = "makeMoveNotationData")
    fun makeMoveNotationTests(expectedValue: String, activeColor: Boolean, moveAction: MoveAction) {

        val castling = Castling( whiteKingside = true,whiteQueenside = true,blackKingside = true,blackQueenside = true)
        val enPassantTarget = if (!activeColor) Pair(6, 2) else null
        val position = Position(testingPlacements, activeColor, castling, enPassantTarget, 0)

        val actualValue = makeMoveNotation(position, moveAction)

        assertEquals(actualValue, expectedValue)
    }

    private val testingPlacements: Array<Array<Piece>> = arrayOf(
        arrayOf(EMPTY     , EMPTY      , EMPTY, EMPTY      , BLACK_ROOK  , EMPTY     , WHITE_BISHOP, BLACK_ROOK),
        arrayOf(EMPTY     , WHITE_QUEEN, EMPTY, WHITE_QUEEN, EMPTY       , EMPTY     , BLACK_PAWN  , EMPTY),
        arrayOf(EMPTY     , EMPTY      , EMPTY, EMPTY      , BLACK_KNIGHT, EMPTY     , EMPTY       , EMPTY),
        arrayOf(EMPTY     , EMPTY      , EMPTY, BLACK_PAWN , EMPTY       , BLACK_PAWN, EMPTY       , EMPTY),
        arrayOf(WHITE_KING, EMPTY      , EMPTY, EMPTY      , EMPTY       , WHITE_PAWN, EMPTY       , EMPTY),
        arrayOf(EMPTY     , EMPTY      , EMPTY, WHITE_QUEEN, EMPTY       , EMPTY     , BLACK_BISHOP, EMPTY),
        arrayOf(EMPTY     , EMPTY      , EMPTY, WHITE_PAWN , BLACK_KNIGHT, WHITE_PAWN, EMPTY       , BLACK_BISHOP),
        arrayOf(WHITE_ROOK, EMPTY      , EMPTY, BLACK_PAWN , EMPTY       , BLACK_KING, WHITE_PAWN  , EMPTY),
    )

}