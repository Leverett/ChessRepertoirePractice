package com.leverett.chessrepertoirepractice.repertoire.mode

import com.leverett.chessrepertoirepractice.repertoire.lines.Chapter
import com.leverett.chessrepertoirepractice.repertoire.lines.LineMove
import com.leverett.chessrepertoirepractice.repertoire.lines.Repertoire
import com.leverett.chessrepertoirepractice.repertoire.representation.State

class BuildMode(val repertoire: Repertoire,
                var chapter: Chapter,
                var currentState: State) {

    fun handleMove(movePgn: String) {
        val existingMoves: List<LineMove> = chapter.getMoves(currentState.position)
        if (!existingMoves.any{it.algMove == movePgn}) {

        }
    }


}