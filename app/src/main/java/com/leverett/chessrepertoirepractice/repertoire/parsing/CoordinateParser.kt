package com.leverett.chessrepertoirepractice.repertoire.parsing

object CoordinateParser {

    fun notationToCoordinate(coord: String) : Pair<Int,Int> {
        // TODO Validate input
        val i: Int = when(coord[0]) {
            'a' -> 0
            'b' -> 1
            'c' -> 2
            'd' -> 3
            'e' -> 4
            'f' -> 5
            'g' -> 6
            'h' -> 7
            else -> -1 //TODO ERROR
        }
        val j: Int = coord[1].toInt() - 1
        return Pair(i, j)
    }

    fun coordinateToNotation(i: Int, j: Int): String {
        if (i < 0 || i > 7 || j < 0 || j > 7) {
            //TODO ERROR
        }
        val file: String = when(i) {
            0 -> "a"
            1 -> "b"
            2 -> "c"
            3 -> "d"
            4 -> "e"
            5 -> "f"
            6 -> "g"
            7 -> "h"
            else -> "" // TODO error
        }
        val rank = (j + 1).toString()
        return file+rank
    }

}