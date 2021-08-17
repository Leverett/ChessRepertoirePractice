package com.leverett.rules.chess.representation

class PositionStatus(val legalMoves: List<Move>, val illegalMoves: List<Move>, val inCheck: Boolean) {

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

    fun findMoveAndStatus(startLoc: Pair<Int,Int>, endLoc: Pair<Int,Int>, promotionPiece: Piece? = null): Pair<Move?, MoveStatus> {
        for (move in legalMoves) {
            if (move.startLoc == startLoc && move.endLoc == endLoc) {
                if (promotionPiece == null || promotionPiece == move.promotion) {
                    return Pair(move, MoveStatus.LEGAL)
                }
            }
        }
        for (move in illegalMoves) {
            if (move.startLoc == startLoc && move.endLoc == endLoc) {
                if (promotionPiece == null || promotionPiece == move.promotion) {
                    return Pair(move, MoveStatus.ILLEGAL)
                }
            }
        }
        return Pair(null, MoveStatus.INVALID)
    }

    fun copy(): PositionStatus {
        return PositionStatus(legalMoves, illegalMoves, inCheck)
    }

}