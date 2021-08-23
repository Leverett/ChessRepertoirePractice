package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.move.LineMove
import com.leverett.rules.chess.representation.Position

open class LineTreeSet(open val lineTrees: MutableList<LineTree>,name: String = "activeRepertoire",  description: String? = null) : LineTree, LineTreeBase(name, description) {

    override fun getMoves(position: Position): List<LineMove> {
        val moves = arrayListOf<LineMove>()
        for (lineTree: LineTree in lineTrees) {
            moves.addAll(lineTree.getMoves(position))
        }
        return moves
    }

    override fun copy(): LineTree {
        return LineTreeSet(lineTrees, name, description)
    }
}