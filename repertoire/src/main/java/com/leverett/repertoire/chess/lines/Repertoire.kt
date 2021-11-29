package com.leverett.repertoire.chess.lines

class Repertoire(lineTrees: MutableList<LineTree>) : LineTreeSet(lineTrees, REPERTOIRE_NAME, null) {

    fun makeActiveRepertoire(): LineTreeSet {
        return LineTreeSet(this.lineTrees.map { it.copy() }.toMutableList(),ACTIVE_REPERTOIRE_NAME)
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