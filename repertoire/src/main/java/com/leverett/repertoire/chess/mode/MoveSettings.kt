package com.leverett.repertoire.chess.mode

class MoveSettings(var bestMoves: Boolean,
                   var theoryOnly: Boolean,
                   var noMistakes: Boolean) {

    var playGambits: Boolean = false
        set(value) {
            playGambits = value
            if (value) {
                avoidGambits = false
            }
        }
    var avoidGambits: Boolean = false
        set(value) {
            avoidGambits = value
            if (value) {
                playGambits = false
            }
        }
}