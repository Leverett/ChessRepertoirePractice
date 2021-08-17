package com.leverett.rules.chess.representation;

class GameHistory(var currentGameState: GameState) {

    val positionHistory: List<GameState> = mutableListOf(currentGameState)

}
