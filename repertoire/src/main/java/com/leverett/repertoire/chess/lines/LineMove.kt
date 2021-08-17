package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.MoveDetails
import com.leverett.rules.chess.parsing.PGNBuilder
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.Position

class LineMove(val previousPosition: Position, val nextPosition: Position, val move: Move, val moveDetails: MoveDetails, algMove: String? = null) {

    // Better to make this on construction and store in than do it on demand, saves work overall I think
    val algMove: String = algMove ?: makeAlgMove()

    private fun makeAlgMove(): String {
        val builder = PGNBuilder
        return builder.makeMoveNotation(previousPosition, move)
    }
}