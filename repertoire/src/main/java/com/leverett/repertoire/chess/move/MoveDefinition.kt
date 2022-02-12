package com.leverett.repertoire.chess.move

import com.leverett.rules.chess.representation.MoveAction
import com.leverett.rules.chess.representation.Position

data class MoveDefinition(val previousPosition: Position, val nextPosition: Position, val moveAction: MoveAction) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is MoveDefinition) {
            return false
        }
        return this.previousPosition.statelessPositionHash == other.previousPosition.statelessPositionHash &&
                this.nextPosition.statelessPositionHash == other.nextPosition.statelessPositionHash
    }

    override fun hashCode(): Int {
        return previousPosition.statelessPositionHash.hashCode() + nextPosition.statelessPositionHash.hashCode()
    }

}