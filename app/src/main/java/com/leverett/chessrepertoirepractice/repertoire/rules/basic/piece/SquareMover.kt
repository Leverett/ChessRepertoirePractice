package com.leverett.chessrepertoirepractice.repertoire.rules.basic.piece;

import com.leverett.chessrepertoirepractice.repertoire.representation.Move
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars
import com.leverett.chessrepertoirepractice.repertoire.representation.Position

abstract class SquareMover(i: Int, j: Int, private val directions: Array<Pair<Int,Int>>):
    PieceBase(i, j) {

    override fun candidateMoves(position: Position) : List<Move> {
        val candidateMoves: MutableList<Move> = mutableListOf()
        val activeColor = position.activeColor
        val startCoords = Pair(i,j)
        for (direction in directions) {
            val moveFile = i + direction.first
            if (moveFile in 0..Position.GRID_SIZE) {
                val moveRank = j + direction.second
                if (moveRank in 0..Position.GRID_SIZE) {
                    val locationChar = position.placements[moveFile][moveRank]
                    if (locationChar == PieceChars.EMPTY ||
                        (activeColor && locationChar.isLowerCase()) ||
                        (!activeColor && locationChar.isLowerCase())) {
                        candidateMoves.add(Move(startCoords, Pair(moveFile,moveRank),
                            PieceChars.EMPTY
                        ))
                    }
                }
            }
        }
        return candidateMoves
    }

    override fun threatensCoord(placements: Array<CharArray>, threateningColor: Boolean): Boolean {
        val threateningPieceChar = threateningPieceChar(threateningColor)
        for (direction in directions) {
            val attackerFile = i + direction.first
            if (attackerFile in 0..Position.GRID_SIZE) {
                val attackerRank = j + direction.second
                if (attackerRank in 0..Position.GRID_SIZE) {
                    val locationChar = placements[attackerFile][attackerRank]
                    if (locationChar == threateningPieceChar) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
