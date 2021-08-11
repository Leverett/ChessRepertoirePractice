package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.State
import com.leverett.rules.chess.representation.Position

class Chapter(val pgn: String, description: String) : LineTreeBase(description), LineTree {

    private val positionToStates: MutableMap<Position, MutableList<State>> = mutableMapOf() // unlikely to ever have more than one state but who knows
    private val stateToMoves: MutableMap<State, MutableList<LineMove>> = mutableMapOf()

    override fun getMoves(position: Position): List<LineMove> {
        val moves = mutableListOf<LineMove>()
        val states = positionToStates[position]
        if (states != null) {
            for (state: State in states) {
                val stateMoves = stateToMoves[state]
                if (stateMoves != null) {
                    moves.addAll(stateMoves)
                }
            }
        }
        return moves
    }

    fun addMove(move: LineMove) {
        val previousState = move.previousState
        val previousPosition = previousState.position
        val previousStates = positionToStates[previousPosition]
        if (previousStates != null && !previousStates.contains(previousState)) {
            previousStates.add(previousState)
        } else {
            positionToStates[previousPosition] = mutableListOf(previousState)
        }
        val moves = stateToMoves[previousState]
        if (moves != null && !moves.contains(move)) {
            moves.add(move)
        } else {
            stateToMoves[previousState] = mutableListOf(move)
        }

        val nextState = move.nextState
        val nextPosition = previousState.position
        val nextStates = positionToStates[nextPosition]
        if (nextStates != null && !nextStates.contains(nextState)) {
            nextStates.add(nextState)
        } else {
            positionToStates[nextPosition] = mutableListOf(nextState)
        }

    }

    fun removeMove(move: LineMove) {
        val previousState = move.previousState
        val moves = stateToMoves[previousState]
        if (moves != null && moves.contains(move)) {
            moves.remove(move)
            if (moves.isEmpty()) {
                stateToMoves.remove(previousState)
            }
        }

        val nextState = move.nextState
        var otherWaysToState = false
        for (entry in stateToMoves) {
            for (m in entry.value) {
                if (m.nextState == nextState) {
                    otherWaysToState = true
                }
            }
        }
        if (!otherWaysToState) {
            val position = nextState.position
            val states = positionToStates[position].also{states ->
                states?.remove(nextState)
            }
            if (states.isNullOrEmpty()) {
                positionToStates.remove(position)
            }

        }

    }


}