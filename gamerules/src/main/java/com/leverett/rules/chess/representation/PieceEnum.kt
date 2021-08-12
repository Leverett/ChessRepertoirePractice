package com.leverett.rules.chess.representation

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

    enum class PieceType {
        PAWN,
        KNIGHT,
        BISHOP,
        ROOK,
        QUEEN,
        KING,
        EMPTY
    }

    companion object {
        fun getPiece(pieceChar: Char): PieceEnum {
            return when (pieceChar) {
                BLACK_PAWN_CHAR -> BLACK_PAWN
                BLACK_KNIGHT_CHAR -> BLACK_KNIGHT
                BLACK_BISHOP_CHAR -> BLACK_BISHOP
                BLACK_ROOK_CHAR -> BLACK_ROOK
                BLACK_QUEEN_CHAR -> BLACK_QUEEN
                BLACK_KING_CHAR -> BLACK_KING
                WHITE_PAWN_CHAR -> WHITE_PAWN
                WHITE_KNIGHT_CHAR -> WHITE_KNIGHT
                WHITE_BISHOP_CHAR -> WHITE_BISHOP
                WHITE_ROOK_CHAR -> WHITE_ROOK
                WHITE_QUEEN_CHAR -> WHITE_QUEEN
                WHITE_KING_CHAR -> WHITE_KING
                else -> EMPTY
            }
        }

        fun getPiece(color: Boolean, pieceType: PieceType): PieceEnum {
            if (color) {
                return when (pieceType) {
                    PieceType.PAWN -> WHITE_PAWN
                    PieceType.KNIGHT -> WHITE_KNIGHT
                    PieceType.BISHOP -> WHITE_BISHOP
                    PieceType.ROOK -> WHITE_ROOK
                    PieceType.QUEEN -> WHITE_QUEEN
                    PieceType.KING -> WHITE_KING
                    else -> EMPTY
                }
            } else {
                return when (pieceType) {
                    PieceType.PAWN -> BLACK_PAWN
                    PieceType.KNIGHT -> BLACK_KNIGHT
                    PieceType.BISHOP -> BLACK_BISHOP
                    PieceType.ROOK -> BLACK_ROOK
                    PieceType.QUEEN -> BLACK_QUEEN
                    PieceType.KING -> BLACK_KING
                    else -> EMPTY
                }
            }
        }

        val PROMOTION_TYPES = arrayOf(
            PieceType.KNIGHT,
            PieceType.BISHOP,
            PieceType.ROOK,
            PieceType.QUEEN
        )
    }

    override fun toString(): String {
        return pieceChar.toString()
    }

}