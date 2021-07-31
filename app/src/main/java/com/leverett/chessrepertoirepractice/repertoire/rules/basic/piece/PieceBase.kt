package com.leverett.chessrepertoirepractice.repertoire.rules.basic.piece

abstract class PieceBase(val i: Int, val j: Int) : Piece {
    abstract fun threateningPieceChar(threateningColor: Boolean): Char
}