package com.leverett.rules.chess.representation

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

enum class PieceEnum(val color: Boolean, val type: PieceType, val pieceChar: Char) {

    BLACK_PAWN(false, PieceType.PAWN, BLACK_PAWN_CHAR),
    BLACK_KNIGHT(false, PieceType.KNIGHT, BLACK_KNIGHT_CHAR),
    BLACK_BISHOP(false, PieceType.BISHOP, BLACK_BISHOP_CHAR),
    BLACK_ROOK(false, PieceType.ROOK, BLACK_ROOK_CHAR),
    BLACK_QUEEN(false, PieceType.QUEEN, BLACK_QUEEN_CHAR),
    BLACK_KING(false, PieceType.KING, BLACK_KING_CHAR),
    WHITE_PAWN(true, PieceType.PAWN, WHITE_PAWN_CHAR),
    WHITE_KNIGHT(true, PieceType.KNIGHT, WHITE_KNIGHT_CHAR),
    WHITE_BISHOP(true, PieceType.BISHOP, WHITE_BISHOP_CHAR),
    WHITE_ROOK(true, PieceType.ROOK, WHITE_ROOK_CHAR),
    WHITE_QUEEN(true, PieceType.QUEEN, WHITE_QUEEN_CHAR),
    WHITE_KING(true, PieceType.KING, WHITE_KING_CHAR),
    EMPTY(true, PieceType.EMPTY, EMPTY_CHAR);

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
fun getPiece(pieceChar: Char): PieceEnum {
    return when (pieceChar) {
        BLACK_PAWN_CHAR -> PieceEnum.BLACK_PAWN
        BLACK_KNIGHT_CHAR -> PieceEnum.BLACK_KNIGHT
        BLACK_BISHOP_CHAR -> PieceEnum.BLACK_BISHOP
        BLACK_ROOK_CHAR -> PieceEnum.BLACK_ROOK
        BLACK_QUEEN_CHAR -> PieceEnum.BLACK_QUEEN
        BLACK_KING_CHAR -> PieceEnum.BLACK_KING
        WHITE_PAWN_CHAR -> PieceEnum.WHITE_PAWN
        WHITE_KNIGHT_CHAR -> PieceEnum.WHITE_KNIGHT
        WHITE_BISHOP_CHAR -> PieceEnum.WHITE_BISHOP
        WHITE_ROOK_CHAR -> PieceEnum.WHITE_ROOK
        WHITE_QUEEN_CHAR -> PieceEnum.WHITE_QUEEN
        WHITE_KING_CHAR -> PieceEnum.WHITE_KING
        else -> PieceEnum.EMPTY
    }
}

fun getPiece(color: Boolean, pieceType: PieceEnum.PieceType): PieceEnum {
    if (color) {
        return when (pieceType) {
            PieceEnum.PieceType.PAWN -> PieceEnum.WHITE_PAWN
            PieceEnum.PieceType.KNIGHT -> PieceEnum.WHITE_KNIGHT
            PieceEnum.PieceType.BISHOP -> PieceEnum.WHITE_BISHOP
            PieceEnum.PieceType.ROOK -> PieceEnum.WHITE_ROOK
            PieceEnum.PieceType.QUEEN -> PieceEnum.WHITE_QUEEN
            PieceEnum.PieceType.KING -> PieceEnum.WHITE_KING
            else -> PieceEnum.EMPTY
        }
    } else {
        return when (pieceType) {
            PieceEnum.PieceType.PAWN -> PieceEnum.BLACK_PAWN
            PieceEnum.PieceType.KNIGHT -> PieceEnum.BLACK_KNIGHT
            PieceEnum.PieceType.BISHOP -> PieceEnum.BLACK_BISHOP
            PieceEnum.PieceType.ROOK -> PieceEnum.BLACK_ROOK
            PieceEnum.PieceType.QUEEN -> PieceEnum.BLACK_QUEEN
            PieceEnum.PieceType.KING -> PieceEnum.BLACK_KING
            else -> PieceEnum.EMPTY
        }
    }
}

fun getPieceType(pieceTypeChar: Char): PieceEnum.PieceType {
    return when (pieceTypeChar) {
        PAWN_CHAR -> PieceEnum.PieceType.PAWN
        KNIGHT_CHAR -> PieceEnum.PieceType.KNIGHT
        BISHOP_CHAR -> PieceEnum.PieceType.BISHOP
        ROOK_CHAR -> PieceEnum.PieceType.ROOK
        QUEEN_CHAR -> PieceEnum.PieceType.QUEEN
        KING_CHAR -> PieceEnum.PieceType.KING
        else -> PieceEnum.PieceType.EMPTY
    }
}

val PROMOTION_TYPES = arrayOf(
    PieceEnum.PieceType.KNIGHT,
    PieceEnum.PieceType.BISHOP,
    PieceEnum.PieceType.ROOK,
    PieceEnum.PieceType.QUEEN
)