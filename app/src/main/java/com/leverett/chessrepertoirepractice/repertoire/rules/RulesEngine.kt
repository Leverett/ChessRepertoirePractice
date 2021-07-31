package com.leverett.chessrepertoirepractice.repertoire.rules

import com.leverett.chessrepertoirepractice.repertoire.representation.Move
import com.leverett.chessrepertoirepractice.repertoire.representation.MoveStatus
import com.leverett.chessrepertoirepractice.repertoire.representation.Position

interface RulesEngine {

    fun validateMove(move: Move): MoveStatus

    fun isInCheck(): Boolean
    fun isInCheckMate(): Boolean
    fun isInStaleMate(): Boolean

    fun getNextPosition(move: Move): Position
}