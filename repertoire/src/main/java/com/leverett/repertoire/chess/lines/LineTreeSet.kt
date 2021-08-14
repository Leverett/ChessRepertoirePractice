package com.leverett.repertoire.chess.lines

import com.leverett.rules.chess.representation.Position

open class LineTreeSet(name: String, open val lineTrees: List<LineTree>, description: String? = null) : LineTree, LineTreeBase(name, description) {

    override fun getMoves(position: Position): List<LineMove> {
        val moves = arrayListOf<LineMove>()
        for (lineTree: LineTree in lineTrees) {
            moves.addAll(lineTree.getMoves(position))
        }
        return moves
    }
}