package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.PieceEnum.BLACK_BISHOP
import com.leverett.rules.chess.representation.PieceEnum.WHITE_BISHOP
import com.leverett.rules.chess.representation.PieceEnum

class Bishop(i: Int, j: Int) : LineMover(i, j, BISHOP_DIRECTIONS) {
    companion object {
        val BISHOP_DIRECTIONS: Array<Pair<Int,Int>> = arrayOf()
    }

    override fun threateningPiece(threateningColor: Boolean): PieceEnum {
        return if (threateningColor) WHITE_BISHOP else BLACK_BISHOP
    }
}