package com.leverett.repertoire.chess

import com.leverett.rules.chess.representation.Position
import com.leverett.rules.chess.representation.startingPosition

class State(val fen: String,
            val position: Position,
            val details: StateDetails
) {
    companion object {
        val STARTING_STATE = State("", startingPosition(), StateDetails())
    }

}