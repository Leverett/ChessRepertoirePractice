package com.leverett.rules

import com.leverett.rules.chess.representation.PositionStatus
import com.leverett.rules.chess.representation.MoveAction
import com.leverett.rules.chess.representation.MoveStatus
import com.leverett.rules.chess.representation.Position

interface RulesEngine {

    fun positionStatus(position: Position): PositionStatus

    /**
     * First entry is legal moves, second is illegal moves
     */
    fun validMoves(position: Position): Pair<List<MoveAction>,List<MoveAction>>
    fun validateMove(position: Position, move: MoveAction): MoveStatus
    fun isMovePromotion(position: Position, startLoc: Pair<Int,Int>, endLoc: Pair<Int,Int>): Boolean

    fun isInCheck(position: Position): Boolean
    fun isInCheckMate(position: Position): Boolean
    fun isInStaleMate(position: Position): Boolean

    fun getNextPosition(position: Position, moveAction: MoveAction): Position
}