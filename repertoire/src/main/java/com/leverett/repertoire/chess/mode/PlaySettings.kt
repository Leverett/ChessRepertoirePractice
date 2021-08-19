package com.leverett.repertoire.chess.mode

import com.leverett.repertoire.chess.MoveDetails.Tag
import com.leverett.repertoire.chess.MoveDetails.Tag.*
import com.leverett.repertoire.chess.lines.LineMove

class PlaySettings(playerBest: Boolean = false,
                   var playerTheory: Boolean = false,
                   var playerPreferred: Boolean = false,
                   playerGambits: Boolean = false,
                   opponentBest: Boolean = false,
                   var opponentTheory: Boolean = false,
                   opponentMistakes: Boolean = false,
                   opponentGambits: Boolean = false) {

    //TODO probably not needed
//    val playerTags: Collection<Tag>
//        get() {
//            val tags = mutableSetOf<Tag>()
//            if (playerBest) tags.add(BEST)
//            if (playerTheory) tags.add(THEORY)
//            if (playerGambits) tags.add(GAMBIT)
//            if (playerPreferred) tags.add(PREFERRED)
//            return tags
//        }
//
//    val opponentTags: Collection<Tag>
//        get() {
//            val tags = mutableSetOf<Tag>()
//            if (opponentBest) tags.add(BEST)
//            if (opponentTheory) tags.add(THEORY)
//            if (opponentGambits) tags.add(GAMBIT)
//            if (opponentMistakes) tags.add(PREFERRED)
//            return tags
//        }

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
}