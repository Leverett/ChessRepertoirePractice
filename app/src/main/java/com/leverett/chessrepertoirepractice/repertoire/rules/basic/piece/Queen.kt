package com.leverett.chessrepertoirepractice.repertoire.rules.basic.piece

import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars
import com.leverett.chessrepertoirepractice.repertoire.rules.basic.piece.Bishop.Companion.BISHOP_DIRECTIONS
import com.leverett.chessrepertoirepractice.repertoire.rules.basic.piece.Rook.Companion.ROOK_DIRECTIONS

class Queen(i: Int, j: Int) : LineMover(i, j, BISHOP_DIRECTIONS + ROOK_DIRECTIONS) {
    override fun threateningPieceChar(color: Boolean): Char {
        return if (color) PieceChars.WHITE_QUEEN else PieceChars.BLACK_QUEEN
    }
}