package com.leverett.repertoire.chess

import com.leverett.repertoire.chess.lines.*
import com.leverett.repertoire.chess.move.LineMove
import com.leverett.repertoire.chess.settings.Configuration
import com.leverett.repertoire.chess.settings.PlaySettings
import com.leverett.rules.chess.representation.Position
import com.leverett.rules.chess.representation.log

object RepertoireManager {

    const val DEFAULT_CONFIGURATION_NAME = "Default"
    private val DEFAULT_CONFIGURATION = Configuration(DEFAULT_CONFIGURATION_NAME, mutableSetOf(), PlaySettings(), true)

    var repertoire: Repertoire = Repertoire(mutableListOf())

    var currentConfigurationName: String = DEFAULT_CONFIGURATION_NAME
    private val configuration: Configuration
        get() = configurations[currentConfigurationName]!!
    var configurations: MutableMap<String, Configuration> = mutableMapOf(Pair(DEFAULT_CONFIGURATION_NAME, DEFAULT_CONFIGURATION))
    val configurationNames: List<String>
        get() = configurations.keys.toList()

    fun printConfigurationRepertoires() {
        log("printConfigurationRepertoires", currentConfigurationName)
        for (configurationPair in configurations) {
            log(configurationPair.key, configurationPair.value.activeRepertoire)
        }
        log("currentConfiguration", configurations[currentConfigurationName]!!.name)
    }

    private val activeRepertoire
        get() = configuration.activeRepertoire
    val playSettings: PlaySettings
        get() = configuration.playSettings
    val color: Boolean
        get() = configuration.color


    val repertoireSize: Int
        get() = repertoire.lineTrees.size

    fun isFullRepertoire(): Boolean {
        return activeRepertoire.isEmpty() &&
                repertoire.lineTreeNames().containsAll(activeRepertoire) &&
                activeRepertoire.containsAll(repertoire.lineTreeNames())
    }

    fun newConfiguration(configurationName: String, color: Boolean) {
        log("newConfiguration", configurationName)
        configurations[configurationName] =
            Configuration(configurationName, mutableSetOf(), PlaySettings(), color)
        currentConfigurationName = configurationName
        printConfigurationRepertoires()
    }

    fun loadConfiguration(configurationName: String) {
        log("loadConfiguration", configurationName)
        if (configurationNames.contains(configurationName)) {
            log("loadConfiguration", "true")
            currentConfigurationName = configurationName
        }
    }

    fun deleteConfiguration() {
        if (currentConfigurationName != DEFAULT_CONFIGURATION_NAME) {
            configurations.remove(currentConfigurationName)
            loadConfiguration(configurationNames[0])
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
        syncConfigurations()
        return deletedLineTree
    }

    fun getMoves(position: Position): List<LineMove> {
        val allMoves = repertoire.getMoves(position)
        return allMoves.filter { isChapterInActiveRepertoire(it.chapter) }
    }

    private fun isChapterInActiveRepertoire(chapter: Chapter): Boolean {
        return activeRepertoire.contains(chapter.name) ||
                (!chapter.isStandalone() && activeRepertoire.contains(chapter.book!!.name))
    }

    fun getLineTree(i: Int): LineTree {
        return repertoire.lineTrees[i]
    }

    fun isActiveLine(lineTree: LineTree): Boolean {
        return activeRepertoire.contains(lineTree.name)
    }

    fun addActiveLine(lineTree: LineTree) {
        activeRepertoire.add(lineTree.name)
        if (lineTree is Book) {
            clearBook(lineTree)
        }
        if (lineTree is Chapter && !lineTree.isStandalone()) {
            val book = lineTree.book!!
            if (activeRepertoire.containsAll(book.lineTreeNames())) {
                activeRepertoire.removeAll(book.lineTreeNames())
                activeRepertoire.add(book.name)
            }
        }
    }

    fun removeActiveLine(lineTree: LineTree) {
        val name = lineTree.name
        if (lineTree is Book) {
            activeRepertoire.remove(name)
            clearBook(lineTree)
        }
        if (lineTree is Chapter) {
            if (activeRepertoire.contains(name)) {
                activeRepertoire.remove(name)
            } else if (!lineTree.isStandalone()){
                val book = lineTree.book!!
                if (activeRepertoire.contains(book.name)) {
                    activeRepertoire.remove(book.name)
                    for (chapterName in book.lineTreeNames()) {
                        if (chapterName != name) {
                            activeRepertoire.add(chapterName)
                        }
                    }
                }
            }
        }
    }

    fun setFullActiveRepertoire(selectAll: Boolean) {
        activeRepertoire.clear()
        if (selectAll) {
            activeRepertoire.addAll(repertoire.lineTreeNames())
        } else {
            activeRepertoire.clear()
        }
    }

    // Called externally to sync the repertoire with what is found in the remote account/storage
    fun syncRepertoire(lineTrees: Set<LineTree>) {
        repertoire.lineTrees.clear()
        repertoire.lineTrees.addAll(lineTrees)
        syncConfigurations()
    }

    // Called internally to update the other configurations with changes to the repertoire
    private fun syncConfigurations() {
        configurations.values.forEach{
            it.activeRepertoire.removeIf{ lt -> repertoire.findLineTreeByName(lt) == null }
        }
    }

    private fun clearBook(book: Book) {
        book.chapters.forEach{ activeRepertoire.remove(it.name) }
    }

}