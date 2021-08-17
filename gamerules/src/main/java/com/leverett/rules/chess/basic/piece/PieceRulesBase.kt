package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.Piece
import com.leverett.rules.chess.representation.getPiece

abstract class PieceRulesBase(val i: Int, val j: Int) : PieceRules {
    abstract val pieceType: Piece.PieceType

    override fun canMoveToCoordFrom(placements: Array<Array<Piece>>, color: Boolean, enPassantTarget: Pair<Int,Int>?): List<Pair<Int, Int>> {
        return threatensCoord(placements, color, enPassantTarget)
    }
}