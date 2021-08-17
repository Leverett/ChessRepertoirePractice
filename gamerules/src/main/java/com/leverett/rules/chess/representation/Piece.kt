package com.leverett.rules.chess.representation

import com.leverett.rules.chess.basic.piece.*

const val WHITE_PAWN_CHAR = 'P'
const val BLACK_PAWN_CHAR = 'p'
const val WHITE_KNIGHT_CHAR = 'N'
const val BLACK_KNIGHT_CHAR = 'n'
const val WHITE_BISHOP_CHAR = 'B'
const val BLACK_BISHOP_CHAR = 'b'
const val WHITE_ROOK_CHAR = 'R'
const val BLACK_ROOK_CHAR = 'r'
const val WHITE_QUEEN_CHAR = 'Q'
const val BLACK_QUEEN_CHAR = 'q'
const val WHITE_KING_CHAR = 'K'
const val BLACK_KING_CHAR = 'k'
const val EMPTY_CHAR = ' '

const val PAWN_CHAR = 'P'
const val KNIGHT_CHAR = 'N'
const val BISHOP_CHAR = 'B'
const val ROOK_CHAR = 'R'
const val QUEEN_CHAR = 'Q'
const val KING_CHAR = 'K'
const val WHITE = true
const val BLACK = false

enum class Piece(val color: Boolean?, val type: PieceType, val pieceChar: Char) {


    WHITE_PAWN(WHITE, PieceType.PAWN, WHITE_PAWN_CHAR),
    WHITE_KNIGHT(WHITE, PieceType.KNIGHT, WHITE_KNIGHT_CHAR),
    WHITE_BISHOP(WHITE, PieceType.BISHOP, WHITE_BISHOP_CHAR),
    WHITE_ROOK(WHITE, PieceType.ROOK, WHITE_ROOK_CHAR),
    WHITE_QUEEN(WHITE, PieceType.QUEEN, WHITE_QUEEN_CHAR),
    WHITE_KING(WHITE, PieceType.KING, WHITE_KING_CHAR),
    BLACK_PAWN(BLACK, PieceType.PAWN, BLACK_PAWN_CHAR),
    BLACK_KNIGHT(BLACK, PieceType.KNIGHT, BLACK_KNIGHT_CHAR),
    BLACK_BISHOP(BLACK, PieceType.BISHOP, BLACK_BISHOP_CHAR),
    BLACK_ROOK(BLACK, PieceType.ROOK, BLACK_ROOK_CHAR),
    BLACK_QUEEN(BLACK, PieceType.QUEEN, BLACK_QUEEN_CHAR),
    BLACK_KING(BLACK, PieceType.KING, BLACK_KING_CHAR),
    EMPTY(null, PieceType.EMPTY, EMPTY_CHAR);

    enum class PieceType(val pieceTypeChar: Char) {
        PAWN(PAWN_CHAR),
        KNIGHT(KNIGHT_CHAR),
        BISHOP(BISHOP_CHAR),
        ROOK(ROOK_CHAR),
        QUEEN(QUEEN_CHAR),
        KING(KING_CHAR),
        EMPTY(EMPTY_CHAR)
    }

    override fun toString(): String {
        return pieceChar.toString()
    }

}
fun getPiece(pieceChar: Char): Piece {
    return when (pieceChar) {
        WHITE_PAWN_CHAR -> Piece.WHITE_PAWN
        WHITE_KNIGHT_CHAR -> Piece.WHITE_KNIGHT
        WHITE_BISHOP_CHAR -> Piece.WHITE_BISHOP
        WHITE_ROOK_CHAR -> Piece.WHITE_ROOK
        WHITE_QUEEN_CHAR -> Piece.WHITE_QUEEN
        WHITE_KING_CHAR -> Piece.WHITE_KING
        BLACK_PAWN_CHAR -> Piece.BLACK_PAWN
        BLACK_KNIGHT_CHAR -> Piece.BLACK_KNIGHT
        BLACK_BISHOP_CHAR -> Piece.BLACK_BISHOP
        BLACK_ROOK_CHAR -> Piece.BLACK_ROOK
        BLACK_QUEEN_CHAR -> Piece.BLACK_QUEEN
        BLACK_KING_CHAR -> Piece.BLACK_KING
        else -> Piece.EMPTY
    }
}

fun getPiece(color: Boolean, pieceType: Piece.PieceType): Piece {
    if (color == WHITE) {
        return when (pieceType) {
            Piece.PieceType.PAWN -> Piece.WHITE_PAWN
            Piece.PieceType.KNIGHT -> Piece.WHITE_KNIGHT
            Piece.PieceType.BISHOP -> Piece.WHITE_BISHOP
            Piece.PieceType.ROOK -> Piece.WHITE_ROOK
            Piece.PieceType.QUEEN -> Piece.WHITE_QUEEN
            Piece.PieceType.KING -> Piece.WHITE_KING
            else -> Piece.EMPTY
        }
    }
    if (color == BLACK){
        return when (pieceType) {
            Piece.PieceType.PAWN -> Piece.BLACK_PAWN
            Piece.PieceType.KNIGHT -> Piece.BLACK_KNIGHT
            Piece.PieceType.BISHOP -> Piece.BLACK_BISHOP
            Piece.PieceType.ROOK -> Piece.BLACK_ROOK
            Piece.PieceType.QUEEN -> Piece.BLACK_QUEEN
            Piece.PieceType.KING -> Piece.BLACK_KING
            else -> Piece.EMPTY
        }
    }
    return Piece.EMPTY
}

fun getPieceType(pieceTypeChar: Char): Piece.PieceType {
    return when (pieceTypeChar) {
        PAWN_CHAR -> Piece.PieceType.PAWN
        KNIGHT_CHAR -> Piece.PieceType.KNIGHT
        BISHOP_CHAR -> Piece.PieceType.BISHOP
        ROOK_CHAR -> Piece.PieceType.ROOK
        QUEEN_CHAR -> Piece.PieceType.QUEEN
        KING_CHAR -> Piece.PieceType.KING
        else -> Piece.PieceType.EMPTY
    }
}

val PROMOTION_TYPES = arrayOf(
    Piece.PieceType.KNIGHT,
    Piece.PieceType.BISHOP,
    Piece.PieceType.ROOK,
    Piece.PieceType.QUEEN
)

fun getPieceRules(pieceType: Piece.PieceType, endLoc: Pair<Int,Int>): PieceRules? {
    return when (pieceType) {
        Piece.PieceType.PAWN -> Pawn(endLoc.first, endLoc.second)
        Piece.PieceType.KNIGHT -> Knight(endLoc.first, endLoc.second)
        Piece.PieceType.BISHOP -> Bishop(endLoc.first, endLoc.second)
        Piece.PieceType.ROOK -> Rook(endLoc.first, endLoc.second)
        Piece.PieceType.QUEEN -> Queen(endLoc.first, endLoc.second)
        Piece.PieceType.KING -> King(endLoc.first, endLoc.second)
        else -> null
    }
}