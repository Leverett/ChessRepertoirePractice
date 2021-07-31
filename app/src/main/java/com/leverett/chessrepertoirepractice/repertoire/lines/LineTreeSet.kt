package com.leverett.chessrepertoirepractice.repertoire.lines

import com.leverett.chessrepertoirepractice.repertoire.representation.Position

open class LineTreeSet(open val lineTrees: List<LineTree>, description: String) : LineTree, LineTreeBase(description) {

    override fun getMoves(position: Position): List<LineMove> {
        val moves = arrayListOf<LineMove>()
        for (lineTree: LineTree in lineTrees) {
            moves.addAll(lineTree.getMoves(position))
        }
        return moves
    }
}