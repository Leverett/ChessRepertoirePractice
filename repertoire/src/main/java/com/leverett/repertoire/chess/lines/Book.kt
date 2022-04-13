package com.leverett.repertoire.chess.lines

import com.leverett.repertoire.chess.pgn.BASELINE_CHAPTER_NAME

class Book(chapters: MutableList<Chapter>, name: String, description: String? = null) : LineTreeSet<Chapter>(chapters, name, description) {

    val chapters: MutableList<Chapter> = lineTrees
    val baselineChapter: Chapter?
        get() = lineTrees.firstOrNull{it.chapterName == BASELINE_CHAPTER_NAME}

    fun hasBaselineChapter(): Boolean {
        return baselineChapter != null
    }

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