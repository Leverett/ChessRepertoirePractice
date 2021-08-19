package com.leverett.repertoire.chess.mode

enum class MoveResult(val priority: Int) {
    MISTAKE(0),
    INCORRECT(1),
    VALID(2),
    CORRECT(3)
}