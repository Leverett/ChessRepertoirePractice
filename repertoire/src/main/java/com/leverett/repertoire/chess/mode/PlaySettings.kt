package com.leverett.repertoire.chess.mode

import com.leverett.repertoire.chess.MoveDetails.Tag
import com.leverett.repertoire.chess.MoveDetails.Tag.*

class PlaySettings(playerBest: Boolean = false,
                   var playerTheory: Boolean = false,
                   var playerPreferred: Boolean = false,
                   playerGambits: Boolean = false,
                   opponentBest: Boolean = false,
                   var opponentTheory: Boolean = false,
                   opponentMistakes: Boolean = false,
                   opponentGambits: Boolean = false) {

    val playerTags: Collection<Tag>
        get() {
            val tags = mutableSetOf<Tag>()
            if (playerBest) tags.add(BEST)
            if (playerTheory) tags.add(THEORY)
            if (playerGambits) tags.add(GAMBIT)
            if (playerPreferred) tags.add(PREFERRED)
            return tags
        }

    val opponentTags: Collection<Tag>
        get() {
            val tags = mutableSetOf<Tag>()
            if (opponentBest) tags.add(BEST)
            if (opponentTheory) tags.add(THEORY)
            if (opponentGambits) tags.add(GAMBIT)
            if (opponentMistakes) tags.add(PREFERRED)
            return tags
        }

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
}