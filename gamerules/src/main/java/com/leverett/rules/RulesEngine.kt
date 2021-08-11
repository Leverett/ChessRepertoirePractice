package com.leverett.rules

import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.MoveStatus
import com.leverett.rules.chess.representation.Position

interface RulesEngine {

    var position: Position

    fun validateMove(move: Move): MoveStatus
    fun isMovePromotion(start: Pair<Int,Int>, end: Pair<Int,Int>)

    fun isInCheck(): Boolean
    fun isInCheckMate(): Boolean
    fun isInStaleMate(): Boolean

    fun getNextPosition(move: Move): Position
}