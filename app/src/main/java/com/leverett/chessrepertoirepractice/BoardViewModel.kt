package com.leverett.chessrepertoirepractice

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import com.leverett.rules.RulesEngine
import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.representation.*

class BoardViewModel(private val boardFragment: BoardFragment) : ViewModel() {
    // "coord" refers to the coordinates on-screen, based on perspective. Uses x&y vars
    // "loc" refers to the absolute position in the game. Uses i&j vars

    var position : Position = startingPosition()
    var rulesEngine: RulesEngine
    init {
        Log.e("BoardViewModel", "\n" + position.quickDisplay())
        rulesEngine = BasicRulesEngine
    }
    private val placements: Array<Array<PieceEnum>>
        get() {
            return position.placements
        }
    fun pieceAtCoords(x: Int, y: Int): PieceEnum {
        val i = coordToLoc(x)
        val j = coordToLoc(y)
        return placements[i][j]
    }
    val activeColor: Boolean
        get() {
            Log.e("BoardViewModel", position.activeColor.toString())
            return position.activeColor
        }
    var activeSquareCoords: Pair<Int, Int>? = null
    var perspectiveColor = true

    // TODO Make this a setting option
    val lightSquareColor = Color.WHITE
    val darkSquareColor = Color.DKGRAY
    val activeSquareColor = Color.BLUE

    fun findMoveAndStatus(startCoords: Pair<Int,Int>, endCoords: Pair<Int,Int>) : Pair<Move?, MoveStatus> {
        val startLoc = coordsToLoc(startCoords)
        val endLoc = coordsToLoc(endCoords)
        return position.findMoveAndStatus(rulesEngine, startLoc, endLoc)
    }

    fun doMove(move: Move) {
        position = rulesEngine.getNextPosition(position, move)
        activeSquareCoords = null
        boardFragment.updateSquaresToPosition()
    }

    private fun coordsToLoc(coords: Pair<Int,Int>): Pair<Int,Int> {
        return Pair(coordToLoc(coords.first), coordToLoc(coords.second))
    }

    private fun coordToLoc(coord: Int): Int {
        return if (perspectiveColor) coord else GRID_SIZE - coord - 1
    }

}