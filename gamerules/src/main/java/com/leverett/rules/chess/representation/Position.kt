package com.leverett.rules.chess.representation

import com.leverett.rules.chess.parsing.FENParser

class Position(val placements:Array<Array<PieceEnum>>,
               val activeColor: Boolean,
               val castling: Castling,
               val enPassantTarget: Pair<Int,Int>,
               val turn: Int) {

    companion object {
        const val GRID_SIZE = 8
        val NEW_PLACEMENTS: Array<Array<PieceEnum>> = Array(GRID_SIZE) { Array(GRID_SIZE) { PieceEnum.EMPTY } }
        val NO_ENPASSANT_TARGET = Pair(-1,-1)
        val STARTING_POSITION = FENParser.positionFromFen(FENParser.STARTING_FEN)
    }

    fun castleAvailable(side: Boolean): Boolean {
        return castling.castleAvailable(activeColor, side)
    }

    fun copyPlacements(): Array<Array<PieceEnum>> {
        val newPlacements = NEW_PLACEMENTS
        for (i in 0..GRID_SIZE) {
            for (j in 0..GRID_SIZE) {
                newPlacements[i][j] = placements[i][j]
            }
        }
        return newPlacements
    }
}