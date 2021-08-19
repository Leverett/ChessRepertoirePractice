package com.leverett.rules.chess.representation;

class GameHistory(private val startingGameState: GameState) {

    private val gameStatesForward: MutableMap<String,GameState> = mutableMapOf()
    private val gameStatesBackwards: MutableMap<String,GameState> = mutableMapOf()
    var currentGameState = startingGameState

    val displayValues: List<String>
        get() {
            val result = mutableListOf<String>()
            var gameState: GameState? = startingGameState
            while (gameState != null) {
                if (gameState!!.fen != "")
                    result.add(gameState.algMove)
                gameState = gameStatesForward[gameState.fen]
            }
            return result
    }

    fun addGameState(gameState: GameState) {
        gameStatesForward[currentGameState.fen] = gameState.copy()
        gameStatesBackwards[gameState.fen] = currentGameState.copy()
    }

    fun nextGameState(): GameState? {
        return gameStatesForward[currentGameState.fen]
    }

    fun previousGameState(): GameState? {
        return gameStatesBackwards[currentGameState.fen]
    }

    fun stringToNow(): String {
        var result = ""
        var gameState: GameState? = gameStatesForward[startingGameState.fen]
        if (gameState != null) {
            do {
                if (!gameState!!.position.activeColor) {
                    result += gameState.position.turn.toString() + ". "
                }
                result += gameState.algMove + " "
                if (gameState == currentGameState) {
                    break
                }
                gameState = gameStatesForward[gameState.fen]
            } while (gameState != null)
        }
        return result
    }

    override fun toString(): String {
        var result = ""
        var gameState: GameState? = gameStatesForward[startingGameState.fen]
        while (gameState != null)
        {
            if (!gameState.position.activeColor) {
                result += gameState.position.turn.toString() + ". "
            }
            result += gameState.algMove + " "
            gameState = gameStatesForward[gameState.fen]
        }
        return result
    }

}
