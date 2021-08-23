package com.leverett.repertoire.chess.lines

abstract class LineTreeBase(val name: String, var description: String?) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is LineTree) {
            return false
        }
        return this.name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}