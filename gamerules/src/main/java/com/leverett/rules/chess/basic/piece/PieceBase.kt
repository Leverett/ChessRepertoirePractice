package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.PieceEnum

abstract class PieceBase(val i: Int, val j: Int) : Piece {
    abstract fun threateningPiece(threateningColor: Boolean): PieceEnum
}