package com.leverett.chessrepertoirepractice.repertoire.rules.basic.piece

import com.leverett.chessrepertoirepractice.repertoire.representation.Move
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.BISHOP
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.EMPTY
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.KNIGHT
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.PAWN
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.PROMOTION_CHARS
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.QUEEN
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.ROOK
import com.leverett.chessrepertoirepractice.repertoire.representation.Position
import com.leverett.chessrepertoirepractice.repertoire.representation.Position.Companion.GRID_SIZE

class Pawn(i: Int, j: Int) : PieceBase(i, j) {

    private val startCoord: Pair<Int,Int> = Pair(i,j)

    override fun candidateMoves(position: Position) : List<Move> {
        val candidateMoves: MutableList<Move> = mutableListOf()
        val activeColor = position.activeColor
        val direction = if (activeColor) 1 else -1
        val moveRank = j + direction
        val promotion = (moveRank == 0 || moveRank == GRID_SIZE - 1)

        // Pushes
        if (position.placements[i][moveRank] == EMPTY) {
            // Promotions
            if (promotion) {
                for (promotionPiece in PROMOTION_CHARS)  {
                    val promotionChar = if (activeColor) promotionPiece.toUpperCase() else promotionPiece.toLowerCase()
                    candidateMoves.add(Move(startCoord, Pair(i,moveRank), promotionChar))
                }
            }
            // Regular
            else {
                candidateMoves.add(Move(startCoord, Pair(i,moveRank), EMPTY))
            }
            // Double pushes
            val homeRank = if (activeColor) 1 else 6
            if (j == homeRank && position.placements[i][j + (2*direction)] == EMPTY) {
                candidateMoves.add(Move(startCoord, Pair(i,j+(2*direction)), ' '))
            }
        }
        // Captures to the left
        val leftFile = i - 1
        if (leftFile > 0 &&
            (activeColor && position.placements[leftFile][moveRank].isLowerCase() || (!activeColor && position.placements[leftFile][moveRank].isUpperCase()))) {
            // Promotions
            if (promotion) {
                for (promotionPiece in PROMOTION_CHARS)  {
                    val promotionChar = if (activeColor) promotionPiece.toUpperCase() else promotionPiece.toLowerCase()
                    candidateMoves.add(Move(startCoord, Pair(leftFile,moveRank), promotionChar))
                }
            }
            // Regular
            else {
                candidateMoves.add(Move(startCoord, Pair(leftFile,moveRank), EMPTY))
            }
        }
        // Captures to the right
        val rightFile = i + 1
        if (rightFile < GRID_SIZE &&
            (activeColor && position.placements[rightFile][moveRank].isLowerCase() || (!activeColor && position.placements[rightFile][moveRank].isUpperCase()))) {
            // Promotions
            if (promotion) {
                for (promotionPiece in PROMOTION_CHARS)  {
                    val promotionChar = if (activeColor) promotionPiece.toUpperCase() else promotionPiece.toLowerCase()
                    candidateMoves.add(Move(startCoord, Pair(rightFile,moveRank), promotionChar))
                }
            }
            // Regular
            else {
                candidateMoves.add(Move(startCoord, Pair(rightFile,moveRank), EMPTY))
            }
        }
        // En Passant
        val enPassantTargetFile = position.enPassantTarget.first
        if (enPassantTargetFile != -1 &&
            (enPassantTargetFile + 1 == i) || (enPassantTargetFile - 1 == i) &&
            (position.enPassantTarget.second == moveRank)) {
            candidateMoves.add(Move(startCoord, Pair(enPassantTargetFile,moveRank), EMPTY))
        }
        return candidateMoves
    }

    override fun threatensCoord(placements: Array<CharArray>, threateningColor: Boolean): Boolean {
        val threateningPieceChar = threateningPieceChar(threateningColor)
        val direction = if (threateningColor) -1 else 1
        val attackerRank = j + direction
        val leftFile = i - 1
        val rightFile = i + 1
        if (placements[leftFile][attackerRank] == threateningPieceChar ||
            placements[rightFile][attackerRank] == threateningPieceChar) {
            return true
        }
        return false

    }

    override fun threateningPieceChar(color: Boolean): Char {
        return if (color) PieceChars.WHITE_KNIGHT else PieceChars.BLACK_KNIGHT
    }
}