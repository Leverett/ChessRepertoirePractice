package com.leverett.rules

import com.leverett.rules.chess.representation.PositionStatus
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.MoveStatus
import com.leverett.rules.chess.representation.Position

interface RulesEngine {

    fun positionStatus(position: Position): PositionStatus

    /**
     * First entry is legal moves, second is illegal moves
     */
    fun validMoves(position: Position): Pair<List<Move>,List<Move>>
    fun validateMove(position: Position, move: Move): MoveStatus
    fun isMovePromotion(position: Position, start: Pair<Int,Int>, end: Pair<Int,Int>): Boolean

    fun isInCheck(position: Position): Boolean
    fun isInCheckMate(position: Position): Boolean
    fun isInStaleMate(position: Position): Boolean

    fun getNextPosition(position: Position, move: Move): Position
}