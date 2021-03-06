package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.move.LineMove
import com.leverett.rules.chess.representation.Position

open class LineTreeSet<T : LineTree>(open val lineTrees: MutableList<T>, name: String,  description: String? = null) : LineTreeBase(name, description) {

    override fun getMoves(position: Position): List<LineMove> {
        val moves = arrayListOf<LineMove>()
        for (lineTree: LineTree in lineTrees) {
            moves.addAll(lineTree.getMoves(position))
        }
        return moves
    }

    fun lineTreeNames(): List<String> {
        return lineTrees.map { it.name }
    }

    override fun copy(): LineTree {
        return LineTreeSet(lineTrees.map{it.copy()}.toMutableList(), name, description)
    }
}