package com.leverett.rules.chess.representation

class GameState(val position: Position, val positionStatus: PositionStatus, val algMove: String) {

    val fen: String
        get() {
            return position.fen
        }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is GameState) {
            return false
        }
        return position == other.position
    }

    fun copy(): GameState {
        return GameState(position.copy(), positionStatus.copy(), algMove)
    }


//    val inCheckmate: Boolean
//        get() {
//        get() {
//            return positionStatus.inCheckmate
//        }
//
//    val inStalemate: Boolean
//        get() {
//            return positionStatus.inStalemate
//        }
//
//    fun moveStatus(move: Move): MoveStatus {
//        return positionStatus.moveStatus(move)
//    }
//
//    fun findMoveAndStatus(startLoc: Pair<Int,Int>, endLoc: Pair<Int,Int>, promotionPiece: Piece? = null): Pair<Move?, MoveStatus> {
//        return positionStatus.findMoveAndStatus(startLoc, endLoc, promotionPiece)
//    }
//
//    fun copy(): GameState {
//        return GameState(position.copy(), positionStatus.copy())
//    }
}