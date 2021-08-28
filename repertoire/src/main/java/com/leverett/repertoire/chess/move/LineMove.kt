package com.leverett.repertoire.chess.move

import com.leverett.repertoire.chess.lines.Chapter
import com.leverett.repertoire.chess.lines.LineTree
import com.leverett.repertoire.chess.pgn.makeMoveNotation
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.Position

class LineMove(val chapter: Chapter, val previousPosition: Position, val nextPosition: Position, val move: Move, val moveDetails: MoveDetails, algMove: String? = null) {

    val best: Boolean
        get() = moveDetails.best
    val theory: Boolean
        get() = moveDetails.theory
    val gambit: Boolean
        get() = moveDetails.gambit
    val preferred: Boolean
        get() = moveDetails.preferred
    val mistake: Boolean
        get() = moveDetails.mistake

    // Better to make this on construction and store in than do it on demand, saves work overall I think
    val algMove: String = algMove ?: makeAlgMove()

    private fun makeAlgMove(): String {
        return makeMoveNotation(previousPosition, move)
    }
}