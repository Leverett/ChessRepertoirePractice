package com.leverett.repertoire.chess.lines

import com.leverett.rules.chess.representation.Position

interface LineTree {
    fun getMoves(position: Position): List<LineMove>
}