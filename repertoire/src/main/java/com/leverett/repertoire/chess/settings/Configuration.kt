package com.leverett.repertoire.chess.settings

data class Configuration(val name: String, val activeRepertoire: MutableSet<String>, val playSettings: PlaySettings, var color: Boolean)