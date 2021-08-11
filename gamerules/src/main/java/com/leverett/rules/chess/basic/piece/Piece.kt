package com.leverett.rules.chess.basic.piece

import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.Position

interface Piece {

    /**
     * Finds the valid moves for a piece at the initialized coordinates. Does not validate legality (i.e. check)
     * The color Property of the Piece instance is the color of the moving piece
     */
    fun candidateMoves(position: Position): List<Move>

    /**
     * Determines if the initialized location is under threat from a piece of the instance type
     */
    fun threatensCoord(placements: Array<Array<PieceEnum>>, threateningColor: Boolean): Boolean
}