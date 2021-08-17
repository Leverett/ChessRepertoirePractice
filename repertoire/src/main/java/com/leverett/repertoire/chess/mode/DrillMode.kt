package com.leverett.repertoire.chess.mode

import com.leverett.repertoire.chess.lines.LineTree
import com.leverett.repertoire.chess.lines.LineMove
import com.leverett.repertoire.chess.lines.Repertoire
import com.leverett.rules.chess.representation.Piece
import com.leverett.rules.chess.representation.Position
import com.leverett.rules.chess.representation.startingPosition

class DrillMode(var repertoire: Repertoire,
                var currentLineTree: LineTree,
                var currentPosition: Position = startingPosition(),
                var moveRecord: MutableList<LineMove> = mutableListOf(),
                var playingAsWhite: Boolean,
                val moveSettings: MoveSettings
) {

    fun makeOpponentMove(): LineMove {
        val moves = currentLineTree.getMoves(currentPosition)
        var candidateMoves: List<LineMove> = moves

        // Will pick Best Moves and return if there are any
        candidateMoves = if (moveSettings.bestMoves && candidateMoves.any { it.moveDetails?.isBestMove!! }) {
            candidateMoves.filter { it.moveDetails?.isBestMove!! }.also { return doMove(candidateMoves) }
        } else candidateMoves

        // Filters out any non-theory moves if Theory Only is set
        candidateMoves = if (moveSettings.theoryOnly && candidateMoves.any { !it.moveDetails?.isTheory!! }) {
            candidateMoves.filter { it.moveDetails?.isTheory!! }
        } else candidateMoves.also {//TODO indicate that there are no "theory" moves
        }

        // Select for Gambits or to avoid Gambits if the opportunity appears
        candidateMoves = if (moveSettings.playGambits && candidateMoves.any { it.moveDetails?.isGambitLine!! }) {
            candidateMoves.filter { it.moveDetails?.isGambitLine!! }
        } else candidateMoves
        candidateMoves = if (moveSettings.avoidGambits && candidateMoves.any { it.moveDetails?.isGambitLine!! }) {
            candidateMoves.filter { !it.moveDetails?.isGambitLine!! }
        } else candidateMoves

        // Filter out any mistakes
        candidateMoves = if (moveSettings.noMistakes && candidateMoves.any { it.moveDetails?.isMistake!! }) {
            candidateMoves.filter { !it.moveDetails?.isMistake!! }
        } else candidateMoves

        return doMove(candidateMoves)
    }

    // returns false if the move is a mistake so the interface knows to process that
    fun handlePlayerMove(pgnMove: String): MoveResult {
        val moves: List<LineMove> = currentLineTree.getMoves(currentPosition).filter {
            it.algMove == pgnMove && it.previousPosition == currentPosition
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
        return if (move.moveDetails?.isMistake!!) MoveResult.MISTAKE else MoveResult.UNKNOWN
    }

    fun undoMove(move: LineMove) {
        // TODO validate undo
        currentPosition = move.previousPosition
        moveRecord.remove(move)
    }

    private fun doMove(candidateMoves: List<LineMove>): LineMove {
        if (candidateMoves.isEmpty()) {
            //TODO handle no qualified moves
        }
        val move = candidateMoves.random()
        currentPosition = move.nextPosition
        moveRecord.add(move)
        return move
    }

    fun getPgn(): List<String> {
        return moveRecord.map { it.algMove }
    }

    fun getPlacements(): Array<Array<Piece>> {
        return currentPosition.placements
    }




}