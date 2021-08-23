package com.leverett.repertoire.chess.lines

class Repertoire(lineTrees: MutableList<LineTree>) : LineTreeSet(lineTrees, "repertoire", null) {
    fun makeActiveRepertoire(): LineTreeSet {
        return LineTreeSet(this.lineTrees.map { it.copy() }.toMutableList(),"activeRepertoire")
    }
}