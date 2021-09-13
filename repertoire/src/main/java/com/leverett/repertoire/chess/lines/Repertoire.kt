package com.leverett.repertoire.chess.lines

import com.leverett.rules.chess.representation.log

class Repertoire(lineTrees: MutableList<LineTree>) : LineTreeSet(lineTrees, "repertoire", null) {


    fun makeActiveRepertoire(): LineTreeSet {
        log("makeActiveRepertoire", lineTrees.joinToString(",") { it.name })
        return LineTreeSet(this.lineTrees.map { it.copy() }.toMutableList(),"activeRepertoire")
    }

    fun findLineTreeByName(name: String): LineTree? {
        for (lineTree in lineTrees) {
            if (lineTree.name == name) {
                return lineTree
            }
            if (lineTree is Book) {
                for (chapter in lineTree.lineTrees) {
                    if (chapter.name == name) {
                        return chapter
                    }
                }
            }
        }
        return null
    }
}