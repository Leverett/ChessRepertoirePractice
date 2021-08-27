package com.leverett.repertoire.chess

import com.leverett.repertoire.chess.lines.*
import com.leverett.repertoire.chess.move.LineMove
import com.leverett.repertoire.chess.settings.PlaySettings
import com.leverett.rules.chess.representation.Position

object RepertoireManager {

    private lateinit var storeRepertoireFunction: () -> Unit
    private lateinit var storeConfigurationFunction: () -> Unit
    private lateinit var deleteLineTreeFileFunction: (LineTree) -> Unit

    fun setupStorageFunctions(storeRepertoireFunction: () -> Unit,
                              storeConfigurationFunction: () -> Unit,
                              deleteLineTreeFileFunction: (LineTree) -> Unit) {
        this.storeRepertoireFunction = storeRepertoireFunction
        this.storeConfigurationFunction = storeConfigurationFunction
        this.deleteLineTreeFileFunction = deleteLineTreeFileFunction
    }

    var repertoire: Repertoire = Repertoire(mutableListOf())
    private var activeRepertoire: LineTreeSet = repertoire.makeActiveRepertoire()
    fun newActiveRepertoire() {
        activeRepertoire = repertoire.makeActiveRepertoire()
    }
    var playSettings: PlaySettings = PlaySettings()
        set(value) {
            field = value
            storeRepertoireFunction()
        }

    var configurations: MutableMap<String, Pair<List<String>, PlaySettings>> = mutableMapOf()

    val repertoireSize: Int
        get() = repertoire.lineTrees.size

    fun saveConfiguration(configurationName: String) {
        val activeRepertoireNames = activeRepertoire.lineTrees.map{it.name}
        configurations[configurationName] = Pair(activeRepertoireNames, playSettings.copy())
        storeConfigurationFunction()
    }

    fun loadConfiguration(configurationName: String) {
        val configuration = configurations[configurationName]!!

        //TODO validate that all of these are actually still in the repertoire
        val activeRepertoireNames = configuration.first
        val activeLineTrees = activeRepertoireNames.mapNotNull { repertoire.findLineTreeByName(it) }.toMutableList()
        activeRepertoire = LineTreeSet(activeLineTrees)
        playSettings = configuration.second.copy()
    }

    fun deleteConfiguration(configurationName: String) {
        configurations.remove(configurationName)
        storeConfigurationFunction()
    }

    fun deleteLineTree(lineTree: LineTree, chapter: Chapter? = null) {
        if (chapter != null) {
            val book = repertoire.lineTrees.find{it == lineTree} as Book
            book.lineTrees.remove(chapter)
            if (book.lineTrees.isEmpty()) {
                deleteLineTreeFileFunction(book)
                repertoire.lineTrees.remove(book)
                removeActiveLine(book)
            } else {
                removeActiveLine(chapter)
                storeRepertoireFunction()
            }
        } else {
            repertoire.lineTrees.remove(lineTree)
            deleteLineTreeFileFunction(lineTree)
        }
    }

    fun addToRepertoire(lineTree: LineTree) {
        repertoire.lineTrees.remove(lineTree)
        repertoire.lineTrees.add(lineTree)
        storeRepertoireFunction()
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
    }

    private fun clearBook(book: Book) {
        book.lineTrees.forEach{ activeRepertoire.lineTrees.remove(it) }
    }

}