package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.Piece
import com.leverett.rules.chess.representation.Position

abstract class PieceRulesBase(val i: Int, val j: Int) : PieceRules {
    abstract val pieceType: Piece.PieceType

    override fun canMoveToCoordFrom(position: Position, color: Boolean, enPassantTarget: Pair<Int,Int>?): List<Pair<Int, Int>> {
        val rulesEngine = BasicRulesEngine
        val candidateCoords = threatensCoord(position.placements, color, enPassantTarget)
        return candidateCoords.filter { rulesEngine.isMoveLegal(Move(it, Pair(i, j), null), position) }
    }
}