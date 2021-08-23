package com.leverett.repertoire.chess.move

enum class MoveResult(val priority: Int) {
    MISTAKE(0),
    INCORRECT(1),
    VALID(2),
    CORRECT(3)
}