package com.leverett.rules.chess.representation

class GameState(val position: Position, val positionStatus: PositionStatus, val moveAction: MoveAction?, val algMove: String) {

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

    override fun hashCode(): Int {
        return position.hashCode()
    }

    fun copy(): GameState {
        return if (moveAction == null) GameState(position.copy(), positionStatus.copy(), null, algMove)
            else GameState(position.copy(), positionStatus.copy(), moveAction.copy(), algMove)
    }

}