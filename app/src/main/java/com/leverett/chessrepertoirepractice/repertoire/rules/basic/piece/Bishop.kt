package com.leverett.chessrepertoirepractice.repertoire.rules.basic.piece

import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.BLACK_BISHOP
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.WHITE_BISHOP

class Bishop(i: Int, j: Int) : LineMover(i, j, BISHOP_DIRECTIONS) {
    companion object {
        val BISHOP_DIRECTIONS: Array<Pair<Int,Int>> = arrayOf()
    }

    override fun threateningPieceChar(threateningColor: Boolean): Char {
        return if (threateningColor) WHITE_BISHOP else BLACK_BISHOP
    }
}