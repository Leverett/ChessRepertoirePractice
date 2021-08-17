package com.leverett.repertoire.chess.mode

class PlaySettings(playerBest: Boolean = false,
                   var playerTheory: Boolean = false,
                   var playerPreferred: Boolean = false,
                   playerGambits: Boolean = false,
                   opponentBest: Boolean = false,
                   var opponentTheory: Boolean = false,
                   opponentMistakes: Boolean = false,
                   opponentGambits: Boolean = false) {

    var playerBest: Boolean = playerBest
        set(value) {
            field = value
            if (value) {
                playerGambits = false
            }
        }

    var playerGambits: Boolean = playerBest
        set(value) {
            field = value
            if (value) {
                playerBest = false
            }
        }

    var opponentBest: Boolean = opponentBest
        set(value) {
            field = value
            if (value) {
                opponentMistakes = false
                opponentGambits = false
            }
        }

    var opponentMistakes: Boolean = opponentMistakes
        set(value) {
            field = value
            if (value) {
                opponentBest = false
            }
        }

    var opponentGambits: Boolean = opponentGambits
        set(value) {
            field = value
            if (value) {
                opponentBest = false
            }
        }
}