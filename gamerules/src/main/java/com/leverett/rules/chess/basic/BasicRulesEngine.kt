package com.leverett.rules.chess.basic

import com.leverett.rules.chess.representation.Castling.Companion.BLACK_KINGSIDE_ROOK_HOME_COORD
import com.leverett.rules.chess.representation.Castling.Companion.BLACK_QUEENSIDE_ROOK_HOME_COORD
import com.leverett.rules.chess.representation.Castling.Companion.KINGSIDE_KING_DESTINATION_FILE
import com.leverett.rules.chess.representation.Castling.Companion.KINGSIDE_ROOK_DESTINATION_FILE
import com.leverett.rules.chess.representation.Castling.Companion.KING_HOME_FILE
import com.leverett.rules.chess.representation.Castling.Companion.QUEENSIDE_KING_DESTINATION_FILE
import com.leverett.rules.chess.representation.Castling.Companion.QUEENSIDE_ROOK_DESTINATION_FILE
import com.leverett.rules.chess.representation.Castling.Companion.WHITE_KINGSIDE_ROOK_HOME_COORD
import com.leverett.rules.chess.representation.Castling.Companion.WHITE_QUEENSIDE_ROOK_HOME_COORD
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.PieceEnum.EMPTY
import com.leverett.rules.chess.representation.PieceEnum.*
import com.leverett.rules.chess.representation.PieceEnum.PieceType.*
import com.leverett.rules.RulesEngine
import com.leverett.rules.chess.basic.piece.*
import com.leverett.rules.chess.representation.*
import com.leverett.rules.log
import java.util.stream.Collectors

object BasicRulesEngine: RulesEngine {

    override fun validateMove(position: Position, move: Move): MoveStatus {
        log("VALIDATE MOVE", move.toString())
        if (position.legalMoves(this).contains(move)) {
            return MoveStatus.LEGAL
        }
        if (position.illegalMoves(this).contains(move)) {
            return MoveStatus.ILLEGAL
        }
        return MoveStatus.INVALID
    }

    override fun isMovePromotion(start: Pair<Int, Int>, end: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    override fun isInCheck(position: Position): Boolean {
        if (position.statusCalculated()) {
            return position.gameStatus(this).inCheck
        }
        return inLegalCheck(position)
    }

    override fun isInCheckMate(position: Position): Boolean {
        return position.gameStatus(this).inCheckmate
    }

    override fun isInStaleMate(position: Position): Boolean {
        return position.gameStatus(this).inStalemate
    }

    override fun validMoves(position: Position): Pair<List<Move>, List<Move>> {
        log("VALID MOVES", "HEREEEE")
        val activeColor = position.activeColor
        val pieces: MutableList<Piece> = mutableListOf()
        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {
                val piece = position.placements[i][j]
                if ((piece.color && activeColor) || (!piece.color && !activeColor)) {
                    when (piece.type) {
                        PAWN -> pieces.add(Pawn(i, j))
                        KNIGHT -> pieces.add(Knight(i, j))
                        BISHOP -> pieces.add(Bishop(i, j))
                        ROOK -> pieces.add(Rook(i, j))
                        QUEEN -> pieces.add(Queen(i, j))
                        KING -> pieces.add(King(i, j))
                    }
                }
            }
        }
        val candidateMovesLists: List<List<Move>> = pieces.parallelStream().map { it.candidateMoves(position) }.collect(Collectors.toList())
        val candidateMoves = candidateMovesLists.flatten()
        log("Candidate MOVES", candidateMoves.toString())
        log("Candidate MOVES", candidateMoves.size.toString())
        val legalityList = candidateMoves.parallelStream().map { isMoveLegal(it, position) }.collect(Collectors.toList())
        val legalMovesBuilder: MutableList<Move> = mutableListOf()
        val illegalMovesBuilder: MutableList<Move> = mutableListOf()
        for (i: Int in candidateMoves.indices) {
            if (legalityList[i]) {
                legalMovesBuilder.add(candidateMoves[i])
            }
            else {
                illegalMovesBuilder.add(candidateMoves[i])
            }
        }
        log("LEGAL", legalMovesBuilder.toString())
        log("ILLEGAL", illegalMovesBuilder.toString())
        return Pair(legalMovesBuilder, illegalMovesBuilder)
    }

    fun underAttack(placements: Array<Array<PieceEnum>>, coordinate: Pair<Int,Int>, attackingColor: Boolean) : Boolean {
        val file = coordinate.first
        val rank = coordinate.second

        val pawn = Pawn(file, rank)
        if (pawn.threatensCoord(placements, attackingColor)) {
            return true
        }
        val knight = Knight(file, rank)
        if (knight.threatensCoord(placements, attackingColor)) {
            return true
        }
        val bishop = Bishop(file, rank)
        if (bishop.threatensCoord(placements, attackingColor)) {
            return true
        }
        val rook = Rook(file, rank)
        if (rook.threatensCoord(placements, attackingColor)) {
            return true
        }
        val queen = Queen(file, rank)
        if (queen.threatensCoord(placements, attackingColor)) {
            return true
        }
        val king = King(file, rank)
        if (king.threatensCoord(placements, attackingColor)) {
            return true
        }
        return false
    }

    private fun inLegalCheck(position: Position) : Boolean {
        return underAttack(position.placements, findKing(position.placements, position.activeColor), !position.activeColor)
    }

    private fun inIllegalCheck(position: Position) : Boolean {
        return underAttack(position.placements, findKing(position.placements, !position.activeColor), position.activeColor)
    }

    private fun isMoveLegal(move: Move, position: Position): Boolean {
        val newPosition = getNextPosition(position, move)
        return !inIllegalCheck(newPosition)
    }

    private fun findKing(placements: Array<Array<PieceEnum>>, kingColor: Boolean) : Pair<Int,Int> {
        val king: PieceEnum = if (kingColor) WHITE_KING else BLACK_KING
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


    override fun getNextPosition(position: Position, move: Move): Position {
        if (move == Move.WHITE_KINGSIDE_CASTLE ||
            move == Move.WHITE_QUEENSIDE_CASTLE ||
            move == Move.BLACK_KINGSIDE_CASTLE ||
            move == Move.BLACK_QUEENSIDE_CASTLE
        ) {
            return doCastle(position, move)
        }
        val placements = position.placements
        val piece = placements[move.startLoc.first][move.startLoc.second]
        if (piece.type == PAWN) {
            return doPawnMove(position, move, piece)
        }
        val newPlacements = position.copyPlacements()
        newPlacements[move.startLoc.first][move.startLoc.second] = EMPTY
        newPlacements[move.endLoc.first][move.endLoc.second] = piece
        // TODO Capture events or something should happen here

        val newCastling = calculateNewCastling(move, position.castling, position.activeColor, piece)
        val nextTurn = nextTurn(position)

        return Position(newPlacements, !position.activeColor, newCastling, NO_ENPASSANT_TARGET_COORDINATE, nextTurn)
    }

    private fun doCastle(position: Position, move: Move): Position {
        val newPlacements = position.copyPlacements()
        val castleRank = if (position.activeColor) 0 else GRID_SIZE - 1
        val newCastling = position.castling.copy()
        if (move == Move.WHITE_KINGSIDE_CASTLE) {
            newPlacements[KINGSIDE_KING_DESTINATION_FILE][castleRank] = WHITE_KING
            newPlacements[KINGSIDE_ROOK_DESTINATION_FILE][castleRank] = WHITE_ROOK
            newPlacements[GRID_SIZE-1][castleRank] = EMPTY
            newCastling.whiteKingside = false
            newCastling.whiteQueenside = false
        }
        if (move == Move.WHITE_QUEENSIDE_CASTLE) {
            newPlacements[QUEENSIDE_KING_DESTINATION_FILE][castleRank] = WHITE_QUEEN
            newPlacements[QUEENSIDE_ROOK_DESTINATION_FILE][castleRank] = WHITE_ROOK
            newPlacements[0][castleRank] = EMPTY
            newCastling.whiteKingside = false
            newCastling.whiteQueenside = false
        }
        if (move == Move.BLACK_KINGSIDE_CASTLE) {
            newPlacements[KINGSIDE_KING_DESTINATION_FILE][castleRank] = BLACK_KING
            newPlacements[KINGSIDE_ROOK_DESTINATION_FILE][castleRank] = BLACK_ROOK
            newPlacements[GRID_SIZE-1][castleRank] = EMPTY
            newCastling.blackKingside = false
            newCastling.blackQueenside = false
        }
        if (move == Move.BLACK_QUEENSIDE_CASTLE) {
            newPlacements[QUEENSIDE_KING_DESTINATION_FILE][castleRank] = BLACK_KING
            newPlacements[QUEENSIDE_ROOK_DESTINATION_FILE][castleRank] = BLACK_ROOK
            newPlacements[0][castleRank] = EMPTY
            newCastling.blackKingside = false
            newCastling.blackQueenside = false
        }
        newPlacements[KING_HOME_FILE][castleRank] = EMPTY
        val nextTurn = nextTurn(position)
        return Position(newPlacements, !position.activeColor, newCastling, NO_ENPASSANT_TARGET_COORDINATE, nextTurn)
    }

    private fun doPawnMove(position: Position, move: Move, pawnPiece: PieceEnum): Position {
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

        var enPassantTarget = NO_ENPASSANT_TARGET_COORDINATE
        if (direction * (move.endLoc.second - move.startLoc.second) == 2) {
            val targetFile = move.endLoc.first
            val targetRank = move.endLoc.second - direction
            enPassantTarget = Pair(targetFile, targetRank)
        }

        val newCastling = calculateNewCastling(move, position.castling, position.activeColor, pawnPiece)
        val nextTurn = nextTurn(position)
        return Position(newPlacements, !position.activeColor, newCastling, enPassantTarget, nextTurn)
    }

    private fun calculateNewCastling(move: Move, castling: Castling, activeColor: Boolean, piece: PieceEnum): Castling {
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