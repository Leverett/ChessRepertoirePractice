package com.leverett.chessrepertoirepractice.logging

import android.util.Log
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
import com.leverett.rules.chess.representation.Position.Companion.GRID_SIZE
import com.leverett.rules.chess.representation.Position.Companion.NO_ENPASSANT_TARGET
import com.leverett.rules.RulesEngine
import com.leverett.rules.chess.basic.piece.*
import com.leverett.rules.chess.representation.*
import java.util.stream.Collectors

class BasicRulesEngineLoggable() : RulesEngine {

    constructor(position: Position) : this() {
        Log.e("BasicRulesEngineLoggable", "HEREEEE")
        this.position = position
        Log.e("BasicRulesEngineLoggable", "HEREEEE2")
        calculateValidMoves()
        Log.e("BasicRulesEngineLoggable", "HEREEEE3")
    }

    // TODO store a list of piece objects?

    override var position: Position
        set(value) {
            this.position = value
            calculateValidMoves()
        }
        get() = this.position

    private var legalMoves: List<Move> = listOf()
    private var illegalMoves: List<Move> = listOf()

    override fun validateMove(move: Move): MoveStatus {
        Log.e("BasicRulesEngineLoggable", "HEREEEE4")
        if (legalMoves.contains(move)) {
            return MoveStatus.LEGAL
        }
        if (illegalMoves.contains(move)) {
            return MoveStatus.ILLEGAL
        }
        return MoveStatus.INVALID
    }

    override fun isMovePromotion(start: Pair<Int, Int>, end: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    override fun isInCheck(): Boolean {
        return inLegalCheck(position)
    }

    override fun isInCheckMate(): Boolean {
        return isInCheck() && legalMoves.isEmpty()
    }

    override fun isInStaleMate(): Boolean {
        return !isInCheck() && legalMoves.isEmpty()
    }

    override fun getNextPosition(move: Move): Position {
        return makeNextPosition(position, move)
    }

    private fun calculateValidMoves() {
        Log.e("BasicRulesEngineLoggable", "HEREEEE5")
        val activeColor = position.activeColor
        val pieces: MutableList<Piece> = mutableListOf()
        for (i in 0..GRID_SIZE) {
            for (j in 0..GRID_SIZE) {
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
        val legalityList = candidateMoves.parallelStream().map { isMoveLegal(it, position) }.collect(Collectors.toList())
        val legalMovesBuilder: MutableList<Move> = mutableListOf()
        val illegalMovesBuilder: MutableList<Move> = mutableListOf()
        for (i: Int in 0..candidateMoves.size) {
            if (legalityList[i]) {
                legalMovesBuilder.add(candidateMoves[i])
            }
            else {
                illegalMovesBuilder.add(candidateMoves[i])
            }
        }
        legalMoves = legalMovesBuilder
        illegalMoves = illegalMovesBuilder
        Log.e("BasicRulesEngineLoggable", "HEREEEE6")
    }

    companion object {

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
            val newPosition = makeNextPosition(position, move)
            return inIllegalCheck(newPosition)
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

        private fun makeNextPosition(position: Position, move: Move): Position {
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

            return Position(newPlacements, !position.activeColor, newCastling, NO_ENPASSANT_TARGET, nextTurn)
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
            return Position(newPlacements, !position.activeColor, newCastling, NO_ENPASSANT_TARGET, nextTurn)
        }

        private fun doPawnMove(position: Position, move: Move, pawnPiece: PieceEnum): Position {
            val newPlacements = position.copyPlacements()
            newPlacements[move.startLoc.first][move.startLoc.second] = EMPTY
            val resultPiece = if (move.promotion == EMPTY) pawnPiece else move.promotion
            newPlacements[move.endLoc.first][move.endLoc.second] = resultPiece

            val direction = if (position.activeColor) 1 else -1 //direction active color moves
            if (move.endLoc == position.enPassantTarget) {
                val targetFile = position.enPassantTarget.first
                val targetPawnRank = position.enPassantTarget.second - direction
                newPlacements[targetFile][targetPawnRank] = EMPTY
            }

            var enPassantTarget = NO_ENPASSANT_TARGET
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



}