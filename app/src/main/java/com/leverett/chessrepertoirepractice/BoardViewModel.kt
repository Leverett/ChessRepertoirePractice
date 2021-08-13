package com.leverett.chessrepertoirepractice

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.leverett.chessrepertoirepractice.utils.BoardStyle
import com.leverett.chessrepertoirepractice.utils.PieceStyle
import com.leverett.rules.chess.representation.*

class BoardViewModel : ViewModel() {
    // "coord" refers to the coordinates on-screen, based on perspective. Uses x&y vars
    // "loc" refers to the absolute position in the game. Uses i&j vars

    val squareDimensions = "1:1"

    var position : Position = startingPosition()
    private val placements: Array<Array<PieceEnum>>
        get() {
            return position.placements
        }
    fun pieceAtCoords(coords: Pair<Int, Int>): PieceEnum {
        val i = coordToLoc(coords.first)
        val j = coordToLoc(coords.second)
        return placements[i][j]
    }
    val activeColor: Boolean
        get() {
            return position.activeColor
        }
    var activeSquareCoords: Pair<Int, Int>? = null
    var perspectiveColor = true

    // TODO Make this a setting option
    var pieceStyle = PieceStyle.STANDARD
    var boardStyle = BoardStyle.STANDARD

    fun coordsToLoc(coords: Pair<Int,Int>): Pair<Int,Int> {
        return Pair(coordToLoc(coords.first), coordToLoc(coords.second))
    }

    private fun coordToLoc(coord: Int): Int {
        return if (perspectiveColor) coord else GRID_SIZE - coord - 1
    }

}