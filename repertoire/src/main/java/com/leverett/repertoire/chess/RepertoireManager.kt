package com.leverett.repertoire.chess

import com.leverett.repertoire.chess.lines.LineTreeSet
import com.leverett.repertoire.chess.lines.Repertoire
import com.leverett.repertoire.chess.settings.PlaySettings

object RepertoireManager {

    var repertoire: Repertoire = Repertoire(mutableListOf())
    var activeRepertoire: LineTreeSet = LineTreeSet(mutableListOf())
    var playSettings: PlaySettings = PlaySettings()

    var configurations: MutableMap<String, Pair<LineTreeSet, PlaySettings>> = mutableMapOf()

    val repertoireSize: Int
        get() = repertoire.lineTrees.size

    fun saveConfiguration(configurationName: String) {
        configurations[configurationName] = Pair(activeRepertoire.copy() as LineTreeSet, playSettings.copy())
    }


    fun loadConfiguration(configurationName: String) {
        val configuration = configurations[configurationName]!!
        activeRepertoire = configuration.first.copy() as LineTreeSet
        playSettings = configuration.second.copy()
    }

}