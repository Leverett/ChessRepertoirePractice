package com.leverett.repertoire.chess.lines

class Book(name: String, chapters: List<Chapter>, description: String? = null) : LineTreeSet(name, chapters, description) {
    fun quickDisplay(): String {
        return lineTrees.joinToString("\n", transform = {(it as Chapter).quickDisplay()})
    }
}