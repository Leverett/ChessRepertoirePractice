package com.leverett.repertoire.chess

class MoveDetails(var isTheory: Boolean = false,
                  var isMistake: Boolean = false,
                  var isPreferred: Boolean = false,
                  var isGambitLine: Boolean = false,
                  var isBestMove: Boolean = false,
                  var notes: String = "") {
}