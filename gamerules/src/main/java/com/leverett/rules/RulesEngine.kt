package com.leverett.rules

import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.MoveStatus
import com.leverett.rules.chess.representation.Position

interface RulesEngine {

    fun validMoves(position: Position): Pair<List<Move>,List<Move>>
    fun validateMove(position: Position, move: Move): MoveStatus
    fun isMovePromotion(start: Pair<Int,Int>, end: Pair<Int,Int>)

    fun isInCheck(position: Position): Boolean
    fun isInCheckMate(position: Position): Boolean
    fun isInStaleMate(position: Position): Boolean

    fun getNextPosition(position: Position, move: Move): Position
}