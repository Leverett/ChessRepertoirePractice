package com.leverett.chessrepertoirepractice.repertoire.representation

import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.EMPTY

class Position(val placements:Array<CharArray>,
               val activeColor: Boolean,
               val castling: Castling,
               val enPassantTarget: Pair<Int,Int>) {

    companion object {
        const val GRID_SIZE = 8
        val NEW_PLACEMENTS: Array<CharArray> = Array(GRID_SIZE) { CharArray(GRID_SIZE) { EMPTY } }
        val NO_ENPASSANT_TARGET = Pair(-1,-1)
    }

    fun castleAvailable(side: Boolean): Boolean {
        return castling.castleAvailable(activeColor, side)
    }

    fun copyPlacements(): Array<CharArray> {
        val newPlacements = NEW_PLACEMENTS
        for (i in 0..GRID_SIZE) {
            for (j in 0..GRID_SIZE) {
                newPlacements[i][j] = placements[i][j]
            }
        }
        return newPlacements
    }
}