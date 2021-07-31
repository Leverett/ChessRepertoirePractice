package com.leverett.chessrepertoirepractice.repertoire.representation

import com.leverett.chessrepertoirepractice.repertoire.parsing.FENParser

class State(val fen: String,
            val position: Position,
            val turn: Int,
            val details: StateDetails) {

    companion object {
        val STARTING_STATE = FENParser.stateFromFen(FENParser.STARTING_FEN)
    }
}