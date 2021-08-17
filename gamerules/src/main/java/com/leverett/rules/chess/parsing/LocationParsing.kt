package com.leverett.rules.chess.parsing

fun notationToLocation(loc: String) : Pair<Int,Int> {
    // TODO Validate input
    val i: Int = notationToFile(loc[0])
    val j: Int = loc[1].digitToInt() - 1
    return Pair(i, j)
}

fun notationToFile(notation: Char): Int {
    return when(notation) {
        'a' -> 0
        'b' -> 1
        'c' -> 2
        'd' -> 3
        'e' -> 4
        'f' -> 5
        'g' -> 6
        'h' -> 7
        else -> -1
    }
}

fun locationToNotation(loc: Pair<Int,Int>): String {
    return locationToNotation(loc.first, loc.second)
}

private fun locationToNotation(i: Int, j: Int): String {
    if (i < 0 || i > 7 || j < 0 || j > 7) {
        //TODO ERROR
    }
    val file = fileToNotation(i)
    val rank = (j + 1).toString()
    return file+rank
}

fun fileToNotation(i: Int): Char {
    return when(i) {
        0 -> 'a'
        1 -> 'b'
        2 -> 'c'
        3 -> 'd'
        4 -> 'e'
        5 -> 'f'
        6 -> 'g'
        7 -> 'h'
        else -> ' '
    }
}