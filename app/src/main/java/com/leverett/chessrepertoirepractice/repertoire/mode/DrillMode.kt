package com.leverett.chessrepertoirepractice.repertoire.mode

import com.leverett.chessrepertoirepractice.repertoire.lines.LineTree
import com.leverett.chessrepertoirepractice.repertoire.lines.LineMove
import com.leverett.chessrepertoirepractice.repertoire.lines.Repertoire
import com.leverett.chessrepertoirepractice.repertoire.representation.State

class DrillMode(var repertoire: Repertoire,
                var currentLineTree: LineTree,
                var currentState: State = State.STARTING_STATE,
                var moveRecord: MutableList<LineMove> = mutableListOf(),
                var playingAsWhite: Boolean,
                val moveSettings: MoveSettings) {

    fun makeOpponentMove(): LineMove {
        val moves = currentLineTree.getMoves(currentState.position)
        var candidateMoves: List<LineMove> = moves

        // Will pick Best Moves and return if there are any
        candidateMoves = if (moveSettings.bestMoves && candidateMoves.any { it.nextState.details.isBestMove }) {
            candidateMoves.filter { it.nextState.details.isBestMove }.also { return doMove(candidateMoves) }
        } else candidateMoves

        // Filters out any non-theory moves if Theory Only is set
        candidateMoves = if (moveSettings.theoryOnly && candidateMoves.any { !it.nextState.details.isTheory }) {
            candidateMoves.filter { it.nextState.details.isTheory }
        } else candidateMoves.also {//TODO indicate that there are no "theory" moves
        }

        // Select for Gambits or to avoid Gambits if the opportunity appears
        candidateMoves = if (moveSettings.playGambits && candidateMoves.any { it.nextState.details.isGambitLine }) {
            candidateMoves.filter { it.nextState.details.isGambitLine }
        } else candidateMoves
        candidateMoves = if (moveSettings.avoidGambits && candidateMoves.any { it.nextState.details.isGambitLine }) {
            candidateMoves.filter { !it.nextState.details.isGambitLine }
        } else candidateMoves

        // Filter out any mistakes
        candidateMoves = if (moveSettings.noMistakes && candidateMoves.any { it.nextState.details.isMistake }) {
            candidateMoves.filter { !it.nextState.details.isMistake }
        } else candidateMoves

        return doMove(candidateMoves)
    }

    // returns false if the move is a mistake so the interface knows to process that
    fun handlePlayerMove(pgnMove: String): MoveResult {
        val moves: List<LineMove> = currentLineTree.getMoves(currentState.position).filter {
            it.algMove == pgnMove && it.previousState.position == currentState.position
        }
        if (moves.isNullOrEmpty()) {
            // Handle unknown move
            return MoveResult.UNKNOWN
        }
        if (moves.size > 1) {
            // TODO Error on too many available moves (shouldn't be possible, maybe just automatically fix the structure?)
                //  TODO Should probably throw, so after above todo remove ERROR enum
            return MoveResult.ERROR
        }
        val move = doMove(moves) //There is only one mvoe in the list, so it will do that one
        return if (move.nextState.details.isMistake) MoveResult.MISTAKE else MoveResult.UNKNOWN
    }

    fun undoMove(move: LineMove) {
        // TODO validate undo
        currentState = move.previousState
        moveRecord.remove(move)
    }

    private fun doMove(candidateMoves: List<LineMove>): LineMove {
        if (candidateMoves.isEmpty()) {
            //TODO handle no qualified moves
        }
        val move = candidateMoves.random()
        currentState = move.nextState
        moveRecord.add(move)
        return move
    }

    fun getPgn(): List<String> {
        return moveRecord.map { it.algMove }
    }

    fun getPlacements(): Array<CharArray> {
        return currentState.position.placements
    }




}