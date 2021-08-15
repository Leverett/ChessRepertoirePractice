package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.getPiece

abstract class PieceBase(val i: Int, val j: Int) : Piece {
    abstract val pieceType: PieceEnum.PieceType
    fun threateningPiece(threateningColor: Boolean): PieceEnum {
        return getPiece(threateningColor, pieceType)
    }

    override fun canMoveToCoordFrom(placements: Array<Array<PieceEnum>>, color: Boolean, enPassantTarget: Pair<Int,Int>?): List<Pair<Int, Int>> {
        return threatensCoord(placements, color, enPassantTarget)
    }
}