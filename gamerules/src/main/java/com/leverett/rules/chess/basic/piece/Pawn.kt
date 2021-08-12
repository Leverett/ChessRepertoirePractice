package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.GRID_SIZE
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.PieceEnum.EMPTY
import com.leverett.rules.chess.representation.PieceEnum.PieceType.PAWN
import com.leverett.rules.chess.representation.PieceEnum.Companion.PROMOTION_TYPES
import com.leverett.rules.chess.representation.PieceEnum.Companion.getPiece
import com.leverett.rules.chess.representation.Position

class Pawn(i: Int, j: Int) : PieceBase(i, j) {

    private val startCoord: Pair<Int,Int> = Pair(i,j)

    override fun candidateMoves(position: Position) : List<Move> {
        val candidateMoves: MutableList<Move> = mutableListOf()
        val activeColor = position.activeColor
        val direction = if (activeColor) 1 else -1
        val moveRank = j + direction
        val promotion = isPromotionRank(moveRank)

        // Pushes
        if (position.placements[i][moveRank] == EMPTY) {
            // Promotions
            if (promotion) {
                for (promotionType in PROMOTION_TYPES)  {
                    val promotionPiece = getPiece(activeColor, promotionType)
                    candidateMoves.add(Move(startCoord, Pair(i,moveRank), EMPTY, promotion = promotionPiece))
                }
            }
            // Regular
            else {
                candidateMoves.add(Move(startCoord, Pair(i,moveRank), EMPTY))
            }
            // Double pushes
            val homeRank = if (activeColor) 1 else 6
            if (j == homeRank && position.placements[i][j + (2*direction)] == EMPTY) {
                candidateMoves.add(Move(startCoord, Pair(i,j+(2*direction)), EMPTY))
            }
        }
        // Captures to the left
        val leftFile = i - 1
        if (leftFile > 0) {
            val leftCapturePiece = position.placements[leftFile][moveRank]
            if ((activeColor && !leftCapturePiece.color) || (!activeColor && leftCapturePiece.color)) {
                // Promotions
                if (promotion) {
                    for (promotionType in PROMOTION_TYPES)  {
                        val promotionPiece = getPiece(activeColor, promotionType)
                        candidateMoves.add(Move(startCoord, Pair(leftFile,moveRank), leftCapturePiece, promotion = promotionPiece))
                    }
                }
                // Regular
                else {
                    candidateMoves.add(Move(startCoord, Pair(leftFile,moveRank), leftCapturePiece))
                }
            }
        }
        // Captures to the right
        val rightFile = i + 1
        if (rightFile < GRID_SIZE) {
            val rightCapturePiece = position.placements[rightFile][moveRank]
            if ((activeColor && !rightCapturePiece.color) || (!activeColor && rightCapturePiece.color)) {
                // Promotions
                if (promotion) {
                    for (promotionType in PROMOTION_TYPES)  {
                        val promotionPiece = getPiece(activeColor, promotionType)
                        candidateMoves.add(Move(startCoord, Pair(rightFile,moveRank), rightCapturePiece, promotion = promotionPiece))
                    }
                }
                // Regular
                else {
                    candidateMoves.add(Move(startCoord, Pair(rightFile,moveRank), rightCapturePiece))
                }
            }
        }
        // En Passant
        val enPassantTargetFile = position.enPassantTarget.first
        if (enPassantTargetFile != -1 &&
            (enPassantTargetFile + 1 == i) || (enPassantTargetFile - 1 == i) &&
            (position.enPassantTarget.second == moveRank)) {
            candidateMoves.add(Move(startCoord, Pair(enPassantTargetFile,moveRank), getPiece(!activeColor, PAWN), enPassant = true))
        }
        return candidateMoves
    }

    override fun threatensCoord(placements: Array<Array<PieceEnum>>, threateningColor: Boolean): Boolean {
        val threateningPiece = threateningPiece(threateningColor)
        val direction = if (threateningColor) -1 else 1
        val attackerRank = j + direction
        if (attackerRank >= GRID_SIZE || attackerRank < 0) {
            return false
        }
        val leftFile = i - 1
        if (leftFile >= 0 && placements[leftFile][attackerRank] == threateningPiece) {
            return true
        }
        val rightFile = i + 1
        if ( rightFile < GRID_SIZE && placements[rightFile][attackerRank] == threateningPiece) {
            return true
        }
        return false

    }

    override fun threateningPiece(color: Boolean): PieceEnum {
        return getPiece(color, PAWN)
    }

    companion object {
        fun isPromotionRank(rank: Int): Boolean {
            return (rank == 0 || rank == GRID_SIZE - 1)
        }
    }
}