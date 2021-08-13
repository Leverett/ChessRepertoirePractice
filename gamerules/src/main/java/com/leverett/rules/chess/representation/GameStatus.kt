package com.leverett.rules.chess.representation

import java.util.logging.Level
import java.util.logging.Logger

class GameStatus(private val legalMoves: List<Move>, private val illegalMoves: List<Move>, val inCheck: Boolean) {

    val inCheckmate: Boolean
        get() {
            return legalMoves.isEmpty() && inCheck
        }

    val inStalemate: Boolean
        get() {
            return legalMoves.isEmpty() && !inCheck
        }

    fun moveStatus(move: Move): MoveStatus {
        if (legalMoves.contains(move)) {
            return MoveStatus.LEGAL
        }
        if (illegalMoves.contains(move)) {
            return MoveStatus.ILLEGAL
        }
        return MoveStatus.INVALID
    }

    fun findMoveAndStatus(startLoc: Pair<Int,Int>, endLoc: Pair<Int,Int>, promotionPieceEnum: PieceEnum? = null): Pair<Move?, MoveStatus> {
        for (move in legalMoves) {
            if (move.startLoc == startLoc && move.endLoc == endLoc) {
                if (promotionPieceEnum == null || promotionPieceEnum == move.promotion) {
                    return Pair(move, MoveStatus.LEGAL)
                }
            }
        }
        for (move in illegalMoves) {
            if (move.startLoc == startLoc && move.endLoc == endLoc) {
                if (promotionPieceEnum == null || promotionPieceEnum == move.promotion) {
                    return Pair(move, MoveStatus.ILLEGAL)
                }
            }
        }
        return Pair(null, MoveStatus.INVALID)
    }

}