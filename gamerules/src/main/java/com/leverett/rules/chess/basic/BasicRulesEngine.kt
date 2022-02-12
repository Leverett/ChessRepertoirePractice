package com.leverett.rules.chess.basic

import com.leverett.rules.chess.representation.Piece
import com.leverett.rules.chess.representation.Piece.EMPTY
import com.leverett.rules.chess.representation.Piece.*
import com.leverett.rules.chess.representation.Piece.PieceType.*
import com.leverett.rules.RulesEngine
import com.leverett.rules.chess.basic.piece.*
import com.leverett.rules.chess.representation.*
import java.util.stream.Collectors

object BasicRulesEngine: RulesEngine {

    private val MISSING_KING = Pair(-1,-1)

    override fun validateMove(position: Position, move: MoveAction): MoveStatus {
        return positionStatus(position).moveStatus(move)
    }

    override fun isMovePromotion(position: Position, startLoc: Pair<Int, Int>, endLoc: Pair<Int, Int>): Boolean {
        return isPromotionRank(endLoc.second) &&
            position.pieceAt(startLoc) == getPiece(position.activeColor, PAWN)
    }

    override fun positionStatus(position: Position): PositionStatus {
        val validMoves = validMoves(position)
        val inCheck = isInCheck(position)
        return PositionStatus(validMoves.first, validMoves.second, inCheck)
    }

    override fun isInCheck(position: Position): Boolean {
        return inLegalCheck(position)
    }

    override fun isInCheckMate(position: Position): Boolean {
        return positionStatus(position).inCheckmate
    }

    override fun isInStaleMate(position: Position): Boolean {
        return positionStatus(position).inStalemate
    }

    override fun validMoves(position: Position): Pair<List<MoveAction>, List<MoveAction>> {
        val activeColor = position.activeColor
        val pieceRules: MutableList<PieceRules> = mutableListOf()
        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {
                val piece = position.placements[i][j]
                if (piece.color == activeColor) {
                    when (piece.type) {
                        PAWN -> pieceRules.add(Pawn(i, j))
                        KNIGHT -> pieceRules.add(Knight(i, j))
                        BISHOP -> pieceRules.add(Bishop(i, j))
                        ROOK -> pieceRules.add(Rook(i, j))
                        QUEEN -> pieceRules.add(Queen(i, j))
                        KING -> pieceRules.add(King(i, j))
                        else -> {}
                    }
                }
            }
        }
        val candidateMovesLists: List<List<MoveAction>> = pieceRules.parallelStream().map { it.candidateMoves(position) }.collect(Collectors.toList())
        val candidateMoves = candidateMovesLists.flatten()
        val legalityList = candidateMoves.parallelStream().map { isMoveLegal(it, position) }.collect(Collectors.toList())
        val legalMovesBuilder: MutableList<MoveAction> = mutableListOf()
        val illegalMovesBuilder: MutableList<MoveAction> = mutableListOf()
        for (i: Int in candidateMoves.indices) {
            if (legalityList[i]) {
                legalMovesBuilder.add(candidateMoves[i])
            }
            else {
                illegalMovesBuilder.add(candidateMoves[i])
            }
        }
        return Pair(legalMovesBuilder, illegalMovesBuilder)
    }

    fun underAttack(placements: Array<Array<Piece>>, coordinate: Pair<Int,Int>, attackingColor: Boolean) : Boolean {
        val file = coordinate.first
        val rank = coordinate.second

        val pawn = Pawn(file, rank)
        if (pawn.threatensCoord(placements, attackingColor).isNotEmpty()) {
            return true
        }
        val knight = Knight(file, rank)
        if (knight.threatensCoord(placements, attackingColor).isNotEmpty()) {
            return true
        }
        val bishop = Bishop(file, rank)
        if (bishop.threatensCoord(placements, attackingColor).isNotEmpty()) {
            return true
        }
        val rook = Rook(file, rank)
        if (rook.threatensCoord(placements, attackingColor).isNotEmpty()) {
            return true
        }
        val queen = Queen(file, rank)
        if (queen.threatensCoord(placements, attackingColor).isNotEmpty()) {
            return true
        }
        val king = King(file, rank)
        if (king.threatensCoord(placements, attackingColor).isNotEmpty()) {
            return true
        }
        return false
    }

    private fun inLegalCheck(position: Position) : Boolean {
        val kingLoc = findKing(position.placements, position.activeColor)
        if (kingLoc == MISSING_KING) return false
        return underAttack(position.placements, kingLoc, !position.activeColor)
    }

    private fun inIllegalCheck(position: Position) : Boolean {
        val kingLoc = findKing(position.placements, !position.activeColor)
        if (kingLoc == MISSING_KING) return true
        return underAttack(position.placements, kingLoc, position.activeColor)
    }

    fun isMoveLegal(move: MoveAction, position: Position): Boolean {
        val newPosition = getNextPosition(position, move)
        return !inIllegalCheck(newPosition)
    }

    private fun findKing(placements: Array<Array<Piece>>, kingColor: Boolean) : Pair<Int,Int> {
        val king: Piece = if (kingColor) WHITE_KING else BLACK_KING
        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {
                if (king == placements[i][j]) {
                    return Pair(i,j)
                }
            }
        }
        return Pair(-1, -1)
        //TODO error missing king
    }


    override fun getNextPosition(position: Position, moveAction: MoveAction): Position {
        //TODO some sort of error handling, esp for parsing
        if (moveAction == WHITE_KINGSIDE_CASTLE ||
            moveAction == WHITE_QUEENSIDE_CASTLE ||
            moveAction == BLACK_KINGSIDE_CASTLE ||
            moveAction == BLACK_QUEENSIDE_CASTLE
        ) {
            return doCastle(position, moveAction)
        }
        val placements = position.placements
        val piece = placements[moveAction.startLoc.first][moveAction.startLoc.second]
        if (piece.type == PAWN) {
            return doPawnMove(position, moveAction, piece)
        }
        val newPlacements = position.copyPlacements()
        newPlacements[moveAction.startLoc.first][moveAction.startLoc.second] = EMPTY
        newPlacements[moveAction.endLoc.first][moveAction.endLoc.second] = piece

        val newCastling = calculateNewCastling(moveAction, position.castling, position.activeColor, piece)
        val nextTurn = nextTurn(position)

        return Position(newPlacements, !position.activeColor, newCastling, null, nextTurn)
    }

    private fun doCastle(position: Position, move: MoveAction): Position {
        val newPlacements = position.copyPlacements()
        val castleRank = if (position.activeColor) 0 else GRID_SIZE - 1
        val newCastling = position.castling.copy()
        if (move == WHITE_KINGSIDE_CASTLE) {
            newPlacements[KINGSIDE_KING_DESTINATION_FILE][castleRank] = WHITE_KING
            newPlacements[KINGSIDE_ROOK_DESTINATION_FILE][castleRank] = WHITE_ROOK
            newPlacements[GRID_SIZE-1][castleRank] = EMPTY
            newCastling.whiteKingside = false
            newCastling.whiteQueenside = false
        }
        if (move == WHITE_QUEENSIDE_CASTLE) {
            newPlacements[QUEENSIDE_KING_DESTINATION_FILE][castleRank] = WHITE_KING
            newPlacements[QUEENSIDE_ROOK_DESTINATION_FILE][castleRank] = WHITE_ROOK
            newPlacements[0][castleRank] = EMPTY
            newCastling.whiteKingside = false
            newCastling.whiteQueenside = false
        }
        if (move == BLACK_KINGSIDE_CASTLE) {
            newPlacements[KINGSIDE_KING_DESTINATION_FILE][castleRank] = BLACK_KING
            newPlacements[KINGSIDE_ROOK_DESTINATION_FILE][castleRank] = BLACK_ROOK
            newPlacements[GRID_SIZE-1][castleRank] = EMPTY
            newCastling.blackKingside = false
            newCastling.blackQueenside = false
        }
        if (move == BLACK_QUEENSIDE_CASTLE) {
            newPlacements[QUEENSIDE_KING_DESTINATION_FILE][castleRank] = BLACK_KING
            newPlacements[QUEENSIDE_ROOK_DESTINATION_FILE][castleRank] = BLACK_ROOK
            newPlacements[0][castleRank] = EMPTY
            newCastling.blackKingside = false
            newCastling.blackQueenside = false
        }
        newPlacements[KING_HOME_FILE][castleRank] = EMPTY
        val nextTurn = nextTurn(position)
        return Position(newPlacements, !position.activeColor, newCastling, null, nextTurn)
    }

    private fun doPawnMove(position: Position, move: MoveAction, pawnPiece: Piece): Position {
        val newPlacements = position.copyPlacements()
        newPlacements[move.startLoc.first][move.startLoc.second] = EMPTY
        val resultPiece = move.promotion ?: pawnPiece
        newPlacements[move.endLoc.first][move.endLoc.second] = resultPiece

        val direction = if (position.activeColor) 1 else -1 //direction active color moves
        if (move.endLoc == position.enPassantTarget) {
            val targetFile = position.enPassantTarget.first
            val targetPawnRank = position.enPassantTarget.second - direction
            newPlacements[targetFile][targetPawnRank] = EMPTY
        }

        var enPassantTarget: Pair<Int,Int>? = null
        if (direction * (move.endLoc.second - move.startLoc.second) == 2) {
            val targetFile = move.endLoc.first
            val targetRank = move.endLoc.second - direction
            enPassantTarget = Pair(targetFile, targetRank)
        }

        val newCastling = calculateNewCastling(move, position.castling, position.activeColor, pawnPiece)
        val nextTurn = nextTurn(position)
        return Position(newPlacements, !position.activeColor, newCastling, enPassantTarget, nextTurn)
    }

    private fun calculateNewCastling(move: MoveAction, castling: Castling, activeColor: Boolean, piece: Piece): Castling {
        val newCastling = castling.copy()
        if (piece.type == KING) {
            if (activeColor) {
                newCastling.whiteKingside = false
                newCastling.whiteQueenside = false
            } else {
                newCastling.blackKingside = false
                newCastling.blackQueenside = false
            }
        }
        if (piece.type == ROOK) {
            when (move.startLoc) {
                WHITE_KINGSIDE_ROOK_HOME_COORD -> newCastling.whiteKingside = false
                WHITE_QUEENSIDE_ROOK_HOME_COORD -> newCastling.whiteQueenside = false
                BLACK_KINGSIDE_ROOK_HOME_COORD -> newCastling.blackKingside = false
                BLACK_QUEENSIDE_ROOK_HOME_COORD -> newCastling.blackQueenside = false
            }
        }
        when (move.endLoc) {
            WHITE_KINGSIDE_ROOK_HOME_COORD -> newCastling.whiteKingside = false
            WHITE_QUEENSIDE_ROOK_HOME_COORD -> newCastling.whiteQueenside = false
            BLACK_KINGSIDE_ROOK_HOME_COORD -> newCastling.blackKingside = false
            BLACK_QUEENSIDE_ROOK_HOME_COORD -> newCastling.blackQueenside = false
        }
        return newCastling
    }

    private fun nextTurn(position: Position): Int {
        return if (position.activeColor) position.turn else position.turn + 1
    }



}