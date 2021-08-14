package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.GRID_SIZE
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.PieceEnum.*
import com.leverett.rules.chess.representation.Position

abstract class LineMover(i: Int, j: Int):
    PieceBase(i, j) {

    abstract val directions: Array<Pair<Int,Int>>

    override fun candidateMoves(position: Position): List<Move> {
        val candidateMoves: MutableList<Move> = mutableListOf()
        val activeColor = position.activeColor
        val startCoord = Pair(i, j)
        for (direction in directions) {
            var moveable = true
            var moveFile = i
            var moveRank = j
            while (moveable) {
                moveFile += direction.first
                moveRank += direction.second
                if (moveFile in 0 until GRID_SIZE && moveRank in 0 until GRID_SIZE) {
                    val locationPiece = position.placements[moveFile][moveRank]
                    if (locationPiece == EMPTY) {
                        candidateMoves.add(Move(startCoord, Pair(moveFile, moveRank), EMPTY))
                    }
                    else if ((activeColor && !locationPiece.color) ||
                        (!activeColor && locationPiece.color)) {
                        candidateMoves.add(Move(startCoord, Pair(moveFile, moveRank), locationPiece))
                        moveable = false
                    }
                    else {
                        moveable = false
                    }
                } else {
                    moveable = false
                }
            }
        }
        return candidateMoves
    }

    override fun threatensCoord(placements: Array<Array<PieceEnum>>, threateningColor: Boolean): List<Pair<Int,Int>> {
        val threatens = mutableListOf<Pair<Int,Int>>()
        val threateningPiece = threateningPiece(threateningColor)
        for (direction in directions) {
            var clear = true
            var attackerFile = i
            var attackerRank = j
            while (clear) {
                attackerFile += direction.first
                attackerRank += direction.second
                if (attackerFile in 0 until GRID_SIZE && attackerRank in 0 until GRID_SIZE) {
                    val locationPiece = placements[attackerFile][attackerRank]
                    if (locationPiece == threateningPiece) {
                        threatens.add(Pair(attackerFile,attackerRank))
                    } else if (locationPiece != EMPTY) {
                        clear = false
                    }
                } else {
                    clear = false
                }
            }
        }
        return threatens
    }
}