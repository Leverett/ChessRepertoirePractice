package com.leverett.rules.chess.representation

enum class PieceEnum(val color: Boolean, val type: PieceType, val pieceChar: Char) {

    BLACK_PAWN(false, PieceType.PAWN, PieceChars.BLACK_PAWN),
    BLACK_KNIGHT(false, PieceType.KNIGHT, PieceChars.BLACK_KNIGHT),
    BLACK_BISHOP(false, PieceType.BISHOP, PieceChars.BLACK_BISHOP),
    BLACK_ROOK(false, PieceType.ROOK, PieceChars.BLACK_ROOK),
    BLACK_QUEEN(false, PieceType.QUEEN, PieceChars.BLACK_QUEEN),
    BLACK_KING(false, PieceType.KING, PieceChars.BLACK_KING),
    WHITE_PAWN(true, PieceType.PAWN, PieceChars.BLACK_PAWN),
    WHITE_KNIGHT(true, PieceType.KNIGHT, PieceChars.WHITE_KNIGHT),
    WHITE_BISHOP(true, PieceType.BISHOP, PieceChars.WHITE_BISHOP),
    WHITE_ROOK(true, PieceType.ROOK, PieceChars.WHITE_ROOK),
    WHITE_QUEEN(true, PieceType.QUEEN, PieceChars.WHITE_QUEEN),
    WHITE_KING(true, PieceType.KING, PieceChars.WHITE_KING),
    EMPTY(true, PieceType.EMPTY, PieceChars.EMPTY);

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
                PieceChars.BLACK_PAWN -> BLACK_PAWN
                PieceChars.BLACK_KNIGHT -> BLACK_KNIGHT
                PieceChars.BLACK_BISHOP -> BLACK_BISHOP
                PieceChars.BLACK_ROOK -> BLACK_ROOK
                PieceChars.BLACK_QUEEN -> BLACK_QUEEN
                PieceChars.BLACK_KING -> BLACK_KING
                PieceChars.WHITE_PAWN -> WHITE_PAWN
                PieceChars.WHITE_KNIGHT -> WHITE_KNIGHT
                PieceChars.WHITE_BISHOP -> WHITE_BISHOP
                PieceChars.WHITE_ROOK -> WHITE_ROOK
                PieceChars.WHITE_QUEEN -> WHITE_QUEEN
                PieceChars.WHITE_KING -> WHITE_KING
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

}