package com.leverett.rules.chess.basic.piece;

import com.leverett.rules.chess.representation.*
import com.leverett.rules.chess.representation.Piece.EMPTY

abstract class SquareMover(i: Int, j: Int):
    PieceRulesBase(i, j) {

    abstract val directions: Array<Pair<Int,Int>>

    override fun candidateMoves(position: Position) : List<Move> {
        val candidateMoves: MutableList<Move> = mutableListOf()
        val activeColor = position.activeColor
        val startCoords = Pair(i,j)
        for (direction in directions) {
            val moveFile = i + direction.first
            if (moveFile in 0 until GRID_SIZE) {
                val moveRank = j + direction.second
                if (moveRank in 0 until GRID_SIZE) {
                    val locationPiece = position.placements[moveFile][moveRank]
                    if (locationPiece == EMPTY || activeColor != locationPiece.color!!) {
                        candidateMoves.add(
                            Move(startCoords,
                                Pair(moveFile,moveRank),
                                locationPiece)
                        )
                    }
                }
            }
        }
        return candidateMoves
    }

    override fun threatensCoord(placements: Array<Array<Piece>>, threateningColor: Boolean, enPassantTarget: Pair<Int,Int>?): List<Pair<Int,Int>> {
        val threatens = mutableListOf<Pair<Int,Int>>()
        val threateningPiece = getPiece(threateningColor, pieceType)
        for (direction in directions) {
            val attackerFile = i + direction.first
            if (attackerFile in 0 until GRID_SIZE) {
                val attackerRank = j + direction.second
                if (attackerRank in 0 until GRID_SIZE) {
                    val locationPiece = placements[attackerFile][attackerRank]
                    if (locationPiece == threateningPiece) {
                        threatens.add(Pair(attackerFile,attackerRank))
                    }
                }
            }
        }
        return threatens
    }
}
