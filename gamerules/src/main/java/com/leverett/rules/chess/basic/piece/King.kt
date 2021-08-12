package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.basic.BasicRulesEngine.underAttack
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.PieceEnum.*
import com.leverett.rules.chess.representation.Position
import com.leverett.rules.chess.representation.GRID_SIZE
import com.leverett.rules.chess.representation.PieceEnum

class King(i: Int, j: Int) : SquareMover(i, j, KING_DIRECTIONS) {

    override fun candidateMoves(position: Position) : List<Move> {
        val candidateMoves: MutableList<Move> = super.candidateMoves(position) as MutableList<Move>
        val activeColor = position.activeColor
        val placements = position.placements

        val backRank = if (activeColor) 0 else GRID_SIZE - 1
        if (position.castleAvailable(true) &&
            squaresEmpty(placements, backRank, KINGSIDE_CASTLING_EMPTY_FILE_COORDS) &&
            squaresNotUnderThreat(placements, backRank, KINGSIDE_CASTLING_UNTHREATENED_FILE_COORDS, !activeColor)) {
            candidateMoves.add(Move.WHITE_KINGSIDE_CASTLE)
        }
        if (position.castleAvailable(false) &&
            squaresEmpty(placements, backRank, QUEENSIDE_CASTLING_EMPTY_FILE_COORDS) &&
            squaresNotUnderThreat(placements, backRank, QUEENSIDE_CASTLING_UNTHREATENED_FILE_COORDS, !activeColor)) {
            candidateMoves.add(Move.WHITE_QUEENSIDE_CASTLE)
        }
        if (position.castleAvailable(true) &&
            squaresEmpty(placements, backRank, KINGSIDE_CASTLING_EMPTY_FILE_COORDS) &&
            squaresNotUnderThreat(placements, backRank, KINGSIDE_CASTLING_UNTHREATENED_FILE_COORDS, !activeColor)) {
            candidateMoves.add(Move.BLACK_KINGSIDE_CASTLE)
        }
        if (position.castleAvailable(false) &&
            squaresEmpty(placements, backRank, QUEENSIDE_CASTLING_EMPTY_FILE_COORDS) &&
            squaresNotUnderThreat(placements, backRank, QUEENSIDE_CASTLING_UNTHREATENED_FILE_COORDS, !activeColor)) {
            candidateMoves.add(Move.BLACK_QUEENSIDE_CASTLE)
        }
        return candidateMoves
    }

    private fun squaresEmpty(placements: Array<Array<PieceEnum>>, rank: Int, files: IntArray): Boolean {
        for (file in files) {
            if (placements[file][rank] != EMPTY) {
                return false
            }
        }
        return true
    }
    private fun squaresNotUnderThreat(placements: Array<Array<PieceEnum>>, rank: Int, files: IntArray, threateningColor: Boolean): Boolean {
        for (file in files) {
            if (underAttack(placements, Pair(file, rank), threateningColor)) {
                return false
            }
        }
        return true
    }

    companion object {
        val KING_DIRECTIONS: Array<Pair<Int,Int>> = arrayOf(
            Pair(-1, -1),
            Pair(-1, 0),
            Pair(-1, 1),
            Pair(0, -1),
            Pair(0, 0),
            Pair(0, 1),
            Pair(1, -1),
            Pair(1, 0),
            Pair(1, 1),
        )
        val KINGSIDE_CASTLING_EMPTY_FILE_COORDS: IntArray = intArrayOf(5, 6)
        val QUEENSIDE_CASTLING_EMPTY_FILE_COORDS: IntArray = intArrayOf(1, 2, 3)

        val KINGSIDE_CASTLING_UNTHREATENED_FILE_COORDS: IntArray = intArrayOf(4, 5, 6)
        val QUEENSIDE_CASTLING_UNTHREATENED_FILE_COORDS: IntArray = intArrayOf(1, 2, 3, 4)
    }

    override fun threateningPiece(threateningColor: Boolean): PieceEnum {
        return if (threateningColor) WHITE_KING else BLACK_KING
    }
}