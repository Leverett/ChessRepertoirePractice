package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.*
import com.leverett.rules.chess.representation.Piece.*

abstract class LineMover(i: Int, j: Int):
    PieceRulesBase(i, j) {

    abstract val directions: Array<Pair<Int,Int>>

    override fun candidateMoves(position: Position): List<MoveAction> {
        val candidateMoves: MutableList<MoveAction> = mutableListOf()
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
                    when {
                        locationPiece == EMPTY -> candidateMoves.add(MoveAction(startCoord, Pair(moveFile, moveRank), EMPTY))
                        activeColor != locationPiece.color!! -> {
                            candidateMoves.add(MoveAction(startCoord, Pair(moveFile, moveRank), locationPiece))
                            moveable = false
                        }
                        else ->  moveable = false
                    }
                } else {
                    moveable = false
                }
            }
        }
        return candidateMoves
    }

    override fun threatensCoord(placements: Array<Array<Piece>>, threateningColor: Boolean, enPassantTarget: Pair<Int,Int>?): List<Pair<Int,Int>> {
        val threatens = mutableListOf<Pair<Int,Int>>()
        val threateningPiece = getPiece(threateningColor, pieceType)
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
                        clear = false
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