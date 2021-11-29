package com.leverett.repertoire.chess.settings

import com.leverett.repertoire.chess.move.LineMove
import com.leverett.repertoire.chess.move.MoveResult

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

    var playerGambits: Boolean = playerGambits
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

    fun categorizeLineMove(lineMove: LineMove, lineMoves: Collection<LineMove>, playerMove: Boolean): MoveResult {
        if (lineMove.mistake) return MoveResult.MISTAKE

        if (playerMove) {
            if (playerBest) {
                if (lineMove.best) return MoveResult.CORRECT
                if (lineMoves.any { it.best }) return MoveResult.INCORRECT
            }
            if (playerPreferred) {
                if (lineMove.preferred) return MoveResult.CORRECT
                if (lineMoves.any { it.preferred }) return MoveResult.INCORRECT
            }
            if (playerTheory) {
                if (lineMove.theory) return MoveResult.CORRECT
                if (lineMoves.any { it.theory }) return MoveResult.INCORRECT
            }
            return MoveResult.VALID
        } else {
            if (opponentBest) {
                if (lineMove.best) return MoveResult.CORRECT
                if (lineMoves.any { it.best }) return MoveResult.INCORRECT
            }
            if (opponentTheory) {
                if (lineMove.theory) return MoveResult.CORRECT
                if (lineMoves.any { it.theory }) return MoveResult.INCORRECT
            }
            if (!opponentGambits &&
                lineMove.gambit &&
                lineMoves.any{ !it.gambit }) {
                return MoveResult.INCORRECT
            }
            return MoveResult.VALID
        }
    }

    fun copy(): PlaySettings {
        return PlaySettings(playerBest, playerTheory, playerPreferred, playerGambits, opponentBest, opponentTheory, opponentMistakes, opponentGambits)
    }

    override fun toString(): String {
        return "PlaySettings\n" +
                "playerBest: $playerBest - opponentBest: $opponentBest\n" +
                "playerTheory: $playerTheory - opponentTheory: $opponentTheory\n" +
                "playerGambits: $playerGambits - opponentGambits: $opponentGambits\n" +
                "playerPreferred: $playerPreferred - opponentMistakes: $opponentMistakes"

    }
}