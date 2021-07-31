package com.leverett.chessrepertoirepractice.repertoire.rules.basic.piece

import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.BLACK_ROOK
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.WHITE_ROOK

class Rook(i: Int, j: Int) : LineMover(i, j, ROOK_DIRECTIONS) {
    companion object {
        val ROOK_DIRECTIONS: Array<Pair<Int,Int>> = arrayOf()
    }

    override fun threateningPieceChar(color: Boolean): Char {
        return if (color) WHITE_ROOK else BLACK_ROOK
    }
}