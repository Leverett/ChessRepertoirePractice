package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.move.LineMove
import com.leverett.rules.chess.representation.Position

interface LineTree {
    val name: String
    fun getMoves(position: Position): List<LineMove>
    fun getFirstMoves(): List<LineMove>
    fun copy(): LineTree
}