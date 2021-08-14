package com.leverett.repertoire.chess.mode

import com.leverett.repertoire.chess.lines.Chapter
import com.leverett.repertoire.chess.lines.LineMove
import com.leverett.repertoire.chess.lines.Repertoire

class BuildMode(val repertoire: Repertoire,
                var chapter: Chapter,
                var currentState: State
) {

    fun handleMove(movePgn: String) {
        val existingMoves: List<LineMove> = chapter.getMoves(currentState.position)
        if (!existingMoves.any{it.algMove == movePgn}) {

        }
    }


}