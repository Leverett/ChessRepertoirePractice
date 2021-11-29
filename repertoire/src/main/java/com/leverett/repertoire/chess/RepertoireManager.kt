package com.leverett.repertoire.chess

import com.leverett.repertoire.chess.lines.*
import com.leverett.repertoire.chess.move.LineMove
import com.leverett.repertoire.chess.settings.PlaySettings
import com.leverett.rules.chess.representation.Position

object RepertoireManager {

    var repertoire: Repertoire = Repertoire(mutableListOf())
    private var activeRepertoire: LineTreeSet = repertoire.makeActiveRepertoire()
    fun newActiveRepertoire() {
        activeRepertoire = repertoire.makeActiveRepertoire()
    }
    var playSettings: PlaySettings = PlaySettings()

    var currentConfiguration: String? = null

    var configurations: MutableMap<String, Pair<List<String>, PlaySettings>> = mutableMapOf()
    val configurationNames: List<String>
        get() = configurations.keys.toMutableList()

    val repertoireSize: Int
        get() = repertoire.lineTrees.size

    fun isFullRepertoire(): Boolean {
        return repertoire.lineTreeNames().containsAll(activeRepertoire.lineTreeNames()) &&
                activeRepertoire.lineTreeNames().containsAll(repertoire.lineTreeNames())
    }

    fun newConfiguration(configurationName: String) {
        currentConfiguration = configurationName
        val activeRepertoireNames = activeRepertoire.lineTrees.map { it.name }
        configurations[currentConfiguration!!] =
            Pair(activeRepertoireNames, playSettings.copy())
    }

    fun loadConfiguration(configurationName: String) {
        val configuration = configurations[configurationName]!!
        currentConfiguration = configurationName

        //TODO validate that all of these are actually still in the repertoire
        val activeRepertoireNames = configuration.first
        val activeLineTrees = activeRepertoireNames.mapNotNull { repertoire.findLineTreeByName(it) }.toMutableList()
        activeRepertoire = LineTreeSet(activeLineTrees)
        playSettings = configuration.second.copy()
    }

    fun deleteConfiguration() {
        if (currentConfiguration != null) {
            configurations.remove(currentConfiguration)
            if (configurationNames.isNotEmpty()) {
                loadConfiguration(configurationNames[0])
            } else {
                currentConfiguration = null
            }
        }
    }

    fun deleteLineTree(lineTree: LineTree, chapter: Chapter? = null): LineTree? {
        val deletedLineTree: LineTree? = if (chapter != null) {
            val book = repertoire.lineTrees.find{it == lineTree} as Book
            book.lineTrees.remove(chapter)
            if (book.lineTrees.isEmpty()) {
                repertoire.lineTrees.remove(book)
                removeActiveLine(book)
                lineTree
            } else {
                removeActiveLine(chapter)
                null
            }
        } else {
            repertoire.lineTrees.remove(lineTree)
            lineTree
        }
        syncRepertoireInternal()
        updateConfiguration()
        return deletedLineTree
    }

    fun getMoves(position: Position): List<LineMove> {
        return activeRepertoire.getMoves(position)
    }

    fun getLineTree(i: Int): LineTree {
        return repertoire.lineTrees[i]
    }

    fun isActiveLine(lineTree: LineTree): Boolean {
        return activeRepertoire.lineTrees.contains(lineTree)
    }

    fun addActiveLine(lineTree: LineTree) {
        activeRepertoire.lineTrees.add(lineTree)
        if (lineTree is Book) {
            clearBook(lineTree)
        }
        if (lineTree is Chapter && !lineTree.isStandalone()) {
            val book = lineTree.book!!
            if (activeRepertoire.lineTrees.containsAll(book.lineTrees)) {
                activeRepertoire.lineTrees.removeAll(book.lineTrees)
                activeRepertoire.lineTrees.add(book)
            }
        }
        updateConfiguration()
    }

    fun removeActiveLine(lineTree: LineTree) {
        if (lineTree is Book) {
            activeRepertoire.lineTrees.remove(lineTree)
            clearBook(lineTree)
        }
        if (lineTree is Chapter) {
            if (activeRepertoire.lineTrees.contains(lineTree)) {
                activeRepertoire.lineTrees.remove(lineTree)
            } else if (lineTree.book != null){
                val book = lineTree.book!!
                if (activeRepertoire.lineTrees.contains(lineTree.book!!)) {
                    activeRepertoire.lineTrees.remove(book)
                    for (lt in book.lineTrees) {
                        if (lt != lineTree) {
                            addActiveLine(lt)
                        }
                    }
                }
                else if (activeRepertoire.lineTrees.containsAll(book.lineTrees)) {
                        activeRepertoire.lineTrees.removeAll(book.lineTrees)
                        activeRepertoire.lineTrees.add(book)
                }
            }
        }
        updateConfiguration()
    }

    fun setFullActiveRepertoire(selectAll: Boolean) {
        if (selectAll) {
            newActiveRepertoire()
        } else {
            activeRepertoire.lineTrees.clear()
        }
        updateConfiguration()
    }

    private fun updateConfiguration() {
        if (currentConfiguration != null) {
            configurations[currentConfiguration!!] = Pair(activeRepertoire.lineTreeNames(), playSettings)
        }
    }

    fun syncRepertoire(lineTrees: Set<LineTree>) {
        repertoire.lineTrees.clear()
        repertoire.lineTrees.addAll(lineTrees)
        syncRepertoireInternal()
    }

    private fun syncRepertoireInternal() {
        configurationNames.forEach{
            val configuration = configurations[it] as Pair<List<String>, PlaySettings>
            configurations[it] = Pair(configuration.first.filter { n -> repertoire.findLineTreeByName(n) != null },
                configuration.second)
        }
        activeRepertoire.lineTrees.removeAll{ repertoire.findLineTreeByName(it.name) == null}
    }

    private fun clearBook(book: Book) {
        book.lineTrees.forEach{ activeRepertoire.lineTrees.remove(it) }
    }

}