package com.leverett.repertoire.chess.lines

class Book(chapters: MutableList<LineTree>, name: String, description: String? = null) : LineTreeSet(chapters, name, description) {
    fun quickDisplay(): String {
        return lineTrees.joinToString("\n", transform = {(it as Chapter).quickDisplay()})
    }

    override fun copy(): LineTree {
        val copy = Book(mutableListOf(), name, description)
        lineTrees.forEach {
            val chapterCopy = it.copy()
            (chapterCopy as Chapter).book = copy
            copy.lineTrees.add(it)
        }
        return copy
    }
}