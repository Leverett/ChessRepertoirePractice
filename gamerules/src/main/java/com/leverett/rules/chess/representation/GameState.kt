package com.leverett.rules.chess.representation

class GameState(val position: Position, private val positionStatus: PositionStatus) {

    var nextMove: Move? = null

    val inCheckmate: Boolean
        get() {
            return positionStatus.inCheckmate
        }

    val inStalemate: Boolean
        get() {
            return positionStatus.inStalemate
        }

    fun moveStatus(move: Move): MoveStatus {
        return positionStatus.moveStatus(move)
    }

    fun findMoveAndStatus(startLoc: Pair<Int,Int>, endLoc: Pair<Int,Int>, promotionPiece: Piece? = null): Pair<Move?, MoveStatus> {
        return positionStatus.findMoveAndStatus(startLoc, endLoc, promotionPiece)
    }

    fun copy(): GameState {
        return GameState(position.copy(), positionStatus.copy())
    }
}