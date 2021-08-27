package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.move.LineMove
import com.leverett.rules.chess.representation.startingPosition

abstract class LineTreeBase(override val name: String, var description: String?): LineTree {

    override fun getFirstMoves(): List<LineMove> {
        return getMoves(startingPosition())
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is LineTree || other.javaClass != this.javaClass) {
            return false
        }
        return this.name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}