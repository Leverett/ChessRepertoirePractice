package com.leverett.chessrepertoirepractice

import androidx.lifecycle.ViewModel
import com.leverett.chessrepertoirepractice.utils.BoardStyle
import com.leverett.chessrepertoirepractice.utils.PieceStyle
import com.leverett.rules.chess.representation.*

class BoardViewModel(val gameHistory: GameHistory = newGameHistory()) : ViewModel() {
    // "coord" refers to the coordinates on-screen, based on perspective. Uses x&y vars
    // "loc" refers to the absolute position in the game. Uses i&j vars

    val position: Position
        get() = gameHistory.currentGameState.position
    val positionStatus: PositionStatus
        get() = gameHistory.currentGameState.positionStatus


    fun pieceAtCoords(coords: Pair<Int, Int>): Piece {
        val i = coordToLoc(coords.first)
        val j = coordToLoc(coords.second)
        return position.placements[i][j]
    }
    val activeColor: Boolean
        get() {
            return position.activeColor
        }
    var activeSquareCoords: Pair<Int, Int>? = null
    var perspectiveColor = true
    var canMove = true

    var pieceStyle = PieceStyle.STANDARD
    var boardStyle = BoardStyle.Standard

    fun coordsToLoc(coords: Pair<Int,Int>): Pair<Int,Int> {
        return Pair(coordToLoc(coords.first), coordToLoc(coords.second))
    }

    private fun coordToLoc(coord: Int): Int {
        return if (perspectiveColor) coord else GRID_SIZE - coord - 1
    }

}