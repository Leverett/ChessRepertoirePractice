package com.leverett.rules.chess.basic.piece;

import com.leverett.rules.chess.representation.GRID_SIZE
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.PieceEnum.EMPTY
import com.leverett.rules.chess.representation.Position

abstract class SquareMover(i: Int, j: Int):
    PieceBase(i, j) {

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
                    if (locationPiece == EMPTY ||
                        (activeColor && !locationPiece.color) ||
                        (!activeColor && locationPiece.color)) {
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

    override fun threatensCoord(placements: Array<Array<PieceEnum>>, threateningColor: Boolean): List<Pair<Int,Int>> {
        val threatens = mutableListOf<Pair<Int,Int>>()
        val threateningPiece = threateningPiece(threateningColor)
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
