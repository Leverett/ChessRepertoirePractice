package com.leverett.rules.chess.basic.parsing

import com.leverett.rules.chess.parsing.PGNBuilder
import com.leverett.rules.chess.representation.*
import com.leverett.rules.chess.representation.Piece.*
import org.testng.Assert.assertEquals
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class PGNBuilderTests {

    private val builder = PGNBuilder

    @DataProvider(name = "makeMoveNotationData")
    fun makeMoveNotationData(): Array<Array<Any?>> {
        return arrayOf(
            // regular piece moves from each color
            arrayOf("Rh2", true, Move(Pair(7,0), Pair(7,1), EMPTY)),
            arrayOf("Rc8", false, Move(Pair(0,7), Pair(2,7), EMPTY)),
            // pawn moves
            arrayOf("b6", false, Move(Pair(1,6), Pair(1,5), EMPTY)),
            arrayOf("b5", false, Move(Pair(1,6), Pair(1,4), EMPTY)),
            // regular moves for duplicate pieces on the same file
            arrayOf("Q2b3", true, Move(Pair(1,1), Pair(1,2), EMPTY)),
            arrayOf("Q4b3", true, Move(Pair(1,3), Pair(1,2), EMPTY)),
            // capture moves for duplicate pieces on the same file
            arrayOf("R5xa7", false, Move(Pair(0,4), Pair(0,6), WHITE_BISHOP)),
            arrayOf("R8xa7", false, Move(Pair(0,7), Pair(0,6), WHITE_BISHOP)),
            // regular moves for duplicate pieces on the same rank
            arrayOf("Nce4", false, Move(Pair(2,4), Pair(4,3), EMPTY)),
            arrayOf("Nge4", false, Move(Pair(6,4), Pair(4,3), EMPTY)),
            // capture moves for duplicate pieces on the same rank
            arrayOf("Ncxe6", false, Move(Pair(2,4), Pair(4,5), WHITE_PAWN)),
            arrayOf("Ngxe6", false, Move(Pair(6,4), Pair(4,5), WHITE_PAWN)),
            // moves for duplicate pieces on the same rank and file
            arrayOf("Qb4d2", true, Move(Pair(1,3), Pair(3, 1), EMPTY)),
            // promotions
            arrayOf("h8=Q", true, Move(Pair(7,6), Pair(7, 7), EMPTY, promotion = WHITE_QUEEN)),
            arrayOf("hxg8=Q", true, Move(Pair(7,6), Pair(6, 7), BLACK_BISHOP, promotion = WHITE_QUEEN)),
            // enpassant
            arrayOf("hxg3", false, Move(Pair(7,3), Pair(6, 2), WHITE_PAWN, enPassant = true)),
            // castling
            arrayOf("O-O", true, WHITE_KINGSIDE_CASTLE),
            arrayOf("O-O", false, BLACK_KINGSIDE_CASTLE),
            arrayOf("O-O-O", true, WHITE_QUEENSIDE_CASTLE),
            arrayOf("O-O-O", false, BLACK_QUEENSIDE_CASTLE),

            )
    }

    @Test(dataProvider = "makeMoveNotationData")
    fun makeMoveNotationTests(expectedValue: String, activeColor: Boolean, move: Move) {

        val castling = Castling( whiteKingside = true,whiteQueenside = true,blackKingside = true,blackQueenside = true)
        val enPassantTarget = if (!activeColor) Pair(6, 2) else null
        val position = Position(testingPlacements, activeColor, castling, enPassantTarget, 0)

        val actualValue = builder.makeMoveNotation(position, move)

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