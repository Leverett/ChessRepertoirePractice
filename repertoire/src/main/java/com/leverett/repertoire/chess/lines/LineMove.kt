package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.MoveDetails
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.Position

class LineMove(val previousPosition: Position, val nextPosition: Position, val move: Move, val moveDetails: MoveDetails?) {

    val algMove: String = ""
}