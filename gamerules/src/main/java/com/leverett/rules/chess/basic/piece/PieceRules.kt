package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.MoveAction
import com.leverett.rules.chess.representation.Piece
import com.leverett.rules.chess.representation.Position

interface PieceRules {

    /**
     * Finds the valid moves for a piece at the initialized coordinates. Does not validate legality (i.e. check)
     * The color Property of the Piece instance is the color of the moving piece
     */
    fun candidateMoves(position: Position): List<MoveAction>

    /**
     * Determines where pieces of the instance type threaten the initialized location from, if any
     * This doesn't determine check-wise legality, just whether the piece can move there mechanically
     */
    fun threatensCoord(placements: Array<Array<Piece>>, threateningColor: Boolean, enPassantTarget: Pair<Int,Int>? = null): List<Pair<Int,Int>>

    /**
     * Determines where pieces of the instance type can move to the initialized location from, if any
     * This will check for complete legality, as it is used to generate notation and a pinned piece
     * can confuse the disambiguation of piece moves
     */
    fun canMoveToCoordFrom(position: Position, color: Boolean, enPassantTarget: Pair<Int,Int>? = null): List<Pair<Int,Int>>
}