package com.leverett.chessrepertoirepractice.repertoire.lines

import com.leverett.chessrepertoirepractice.repertoire.representation.Position

interface LineTree {
    fun getMoves(position: Position): List<LineMove>
}