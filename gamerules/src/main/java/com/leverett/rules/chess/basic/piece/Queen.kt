package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.basic.piece.Bishop.Companion.BISHOP_DIRECTIONS
import com.leverett.rules.chess.basic.piece.Rook.Companion.ROOK_DIRECTIONS
import com.leverett.rules.chess.representation.PieceEnum

class Queen(i: Int, j: Int) : LineMover(i, j, BISHOP_DIRECTIONS + ROOK_DIRECTIONS) {
    override fun threateningPiece(color: Boolean): PieceEnum {
        return if (color) PieceEnum.WHITE_QUEEN else PieceEnum.BLACK_QUEEN
    }
}