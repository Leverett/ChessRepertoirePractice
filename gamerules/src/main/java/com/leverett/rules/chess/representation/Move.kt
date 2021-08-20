package com.leverett.rules.chess.representation

import com.leverett.rules.chess.parsing.locationToNotation

class Move(val startLoc: Pair<Int,Int>, val endLoc: Pair<Int,Int>, val capture: Piece,
           val promotion: Piece? = null, val enPassant: Boolean = false) { // TODO is the enpassant val necessary? depends on how move reversal will work

    override fun toString(): String {
        return locationToNotation(startLoc) + " -> " + locationToNotation(endLoc)
//        return "$startLoc -> $endLoc"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Move) {
            return false
        }
        if (startLoc != other.startLoc) {
            return false
        }
        if (endLoc != other.endLoc) {
            return false
        }
        if (capture != other.capture) {
            return false
        }
        if (promotion != other.promotion) {
            return false
        }
        return true
    }

    fun copy(): Move {
        return Move(startLoc.copy(), endLoc.copy(), capture, promotion, enPassant)
    }

    override fun hashCode(): Int {
        return startLoc.hashCode() + endLoc.hashCode() + capture.hashCode() + promotion.hashCode()
    }
}