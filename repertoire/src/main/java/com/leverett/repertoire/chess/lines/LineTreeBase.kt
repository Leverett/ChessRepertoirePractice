package com.leverett.repertoire.chess.lines

abstract class LineTreeBase(override val name: String, var description: String?): LineTree {

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