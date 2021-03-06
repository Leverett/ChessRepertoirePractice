package com.leverett.repertoire.chess.lines

class Repertoire(lineTrees: MutableList<LineTree>) : LineTreeSet<LineTree>(lineTrees, REPERTOIRE_NAME, null) {

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