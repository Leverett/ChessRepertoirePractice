package com.leverett.chessrepertoirepractice.repertoire.rules.basic.piece

import com.leverett.chessrepertoirepractice.repertoire.representation.Move
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.BLACK_KING
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.EMPTY
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.WHITE_KING
import com.leverett.chessrepertoirepractice.repertoire.representation.Position
import com.leverett.chessrepertoirepractice.repertoire.representation.Position.Companion.GRID_SIZE
import com.leverett.chessrepertoirepractice.repertoire.rules.basic.BasicRulesEngine.Companion.underAttack

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

    private fun squaresEmpty(placements: Array<CharArray>, rank: Int, files: IntArray): Boolean {
        for (file in files) {
            if (placements[file][rank] != EMPTY) {
                return false
            }
        }
        return true
    }
    private fun squaresNotUnderThreat(placements: Array<CharArray>, rank: Int, files: IntArray, threateningColor: Boolean): Boolean {
        for (file in files) {
            if (underAttack(placements, Pair(file, rank), threateningColor)) {
                return false
            }
        }
        return true
    }

    companion object {
        val KING_DIRECTIONS: Array<Pair<Int,Int>> = arrayOf(
            Pair(1, 2),
            Pair(1, -2),
            Pair(-1, 2),
            Pair(-1, -2),
            Pair(2, 1),
            Pair(2, -1),
            Pair(-2, 1),
            Pair(-2, -1),
        )
        val KINGSIDE_CASTLING_EMPTY_FILE_COORDS: IntArray = intArrayOf(5, 6)
        val QUEENSIDE_CASTLING_EMPTY_FILE_COORDS: IntArray = intArrayOf(1, 2, 3)

        val KINGSIDE_CASTLING_UNTHREATENED_FILE_COORDS: IntArray = intArrayOf(4, 5, 6)
        val QUEENSIDE_CASTLING_UNTHREATENED_FILE_COORDS: IntArray = intArrayOf(1, 2, 3, 4)
    }

    override fun threateningPieceChar(threateningColor: Boolean): Char {
        return if (threateningColor) WHITE_KING else BLACK_KING
    }
}