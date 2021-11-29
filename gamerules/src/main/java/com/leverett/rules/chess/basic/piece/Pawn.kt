package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.representation.*
import com.leverett.rules.chess.representation.Piece.EMPTY
import com.leverett.rules.chess.representation.Piece.PieceType.PAWN

class Pawn(i: Int, j: Int) : PieceRulesBase(i, j) {

    override val pieceType: Piece.PieceType
        get() = PAWN

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
            val homeRank = homeRank(activeColor)
            if (j == homeRank && position.placements[i][j + (2*direction)] == EMPTY) {
                candidateMoves.add(Move(startCoord, Pair(i,j+(2*direction)), EMPTY))
            }
        }
        // Captures to the left
        val leftFile = i - 1
        if (leftFile > 0) {
            val leftCapturePiece = position.placements[leftFile][moveRank]
            if (leftCapturePiece != EMPTY) {
                if (activeColor != leftCapturePiece.color!!) {
                    // Promotions
                    if (promotion) {
                        for (promotionType in PROMOTION_TYPES) {
                            val promotionPiece = getPiece(activeColor, promotionType)
                            candidateMoves.add(Move(startCoord, Pair(leftFile, moveRank), leftCapturePiece, promotion = promotionPiece)
                            )
                        }
                    }
                    // Regular
                    else {
                        candidateMoves.add(Move(startCoord, Pair(leftFile, moveRank), leftCapturePiece)
                        )
                    }
                }
            }
        }
        // Captures to the right
        val rightFile = i + 1
        if (rightFile < GRID_SIZE) {
            val rightCapturePiece = position.placements[rightFile][moveRank]
            if (rightCapturePiece != EMPTY) {
                if (activeColor != rightCapturePiece.color!!) {
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
        }
        // En Passant
        val enPassantTargetFile = position.enPassantTarget?.first
        if (enPassantTargetFile != null &&
            ((enPassantTargetFile + 1 == i) || (enPassantTargetFile - 1 == i)) &&
            (position.enPassantTarget.second == moveRank)) {
            candidateMoves.add(Move(startCoord, Pair(enPassantTargetFile,moveRank), getPiece(!activeColor, PAWN), enPassant = true))
        }
        return candidateMoves
    }

    override fun threatensCoord(placements: Array<Array<Piece>>, threateningColor: Boolean, enPassantTarget: Pair<Int,Int>?): List<Pair<Int,Int>> {
        val threatens = mutableListOf<Pair<Int,Int>>()
        if (Pair(i,j) != enPassantTarget && (placements[i][j] == EMPTY || placements[i][j].color == threateningColor)) {
            return threatens
        }

        val threateningPiece = getPiece(threateningColor, pieceType)
        val direction = if (threateningColor) -1 else 1
        val attackerRank = j + direction
        if (attackerRank >= GRID_SIZE || attackerRank < 0) {
            return threatens
        }
        val leftFile = i - 1
        if (leftFile >= 0 && placements[leftFile][attackerRank] == threateningPiece) {
            threatens.add(Pair(leftFile, attackerRank))
        }
        val rightFile = i + 1
        if ( rightFile < GRID_SIZE && placements[rightFile][attackerRank] == threateningPiece) {
            threatens.add(Pair(rightFile, attackerRank))
        }
        return threatens
    }

    override fun canMoveToCoordFrom(position: Position, color: Boolean, enPassantTarget: Pair<Int,Int>?): List<Pair<Int, Int>> {
        val rulesEngine = BasicRulesEngine
        val placements = position.placements
        val moves = mutableListOf<Pair<Int,Int>>().also{it.addAll(threatensCoord(placements, color, enPassantTarget))}
        val direction = if (color) -1 else 1
        val pawnPiece = getPiece(color, PAWN)
        val startRank = j + direction
        if (startRank >= GRID_SIZE || startRank < 0) {
            return moves
        }
        if (placements[i][startRank] == pawnPiece) {
            moves.add(Pair(i,startRank))
        }
        val maybeHomeRank = startRank + direction
        if (maybeHomeRank == homeRank(color) && placements[i][startRank] == EMPTY && placements[i][maybeHomeRank] == pawnPiece) {
            moves.add(Pair(i, maybeHomeRank))
        }
        return moves.filter { rulesEngine.isMoveLegal(Move(it, Pair(i, j), null), position) }
    }

    private fun homeRank(color: Boolean): Int {
        return if (color) 1 else 6
    }
}