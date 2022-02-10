package com.leverett.repertoire.chess.lines

class Book(chapters: MutableList<Chapter>, name: String, description: String? = null) : LineTreeSet<Chapter>(chapters, name, description) {

    val chapters: MutableList<Chapter> = lineTrees

    fun quickDisplay(): String {
        return lineTrees.joinToString("\n", transform = {it.quickDisplay()})
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