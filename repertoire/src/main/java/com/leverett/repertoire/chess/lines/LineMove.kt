package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.MoveDetails
import com.leverett.rules.chess.parsing.PGNBuilder
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.Position

class LineMove(val lineTree: LineTree, val previousPosition: Position, val nextPosition: Position, val move: Move, val moveDetails: MoveDetails, algMove: String? = null) {

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
        val builder = PGNBuilder
        return builder.makeMoveNotation(previousPosition, move)
    }
}