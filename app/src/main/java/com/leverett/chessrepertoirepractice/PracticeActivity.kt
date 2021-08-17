package com.leverett.chessrepertoirepractice

import android.os.Bundle
import com.leverett.chessrepertoirepractice.ui.views.PlaySettingButton
import com.leverett.repertoire.chess.lines.LineMove
import com.leverett.repertoire.chess.lines.LineTree
import com.leverett.repertoire.chess.lines.LineTreeSet
import com.leverett.repertoire.chess.mode.MoveResult
import com.leverett.repertoire.chess.mode.PlaySettings
import com.leverett.rules.chess.representation.Move

class PracticeActivity : ChessActivity() {

    override val boardId = R.id.practice_board
    private val boardViewModel: BoardViewModel
        get() = boardFragment.viewModel

    private val playSettings = PlaySettings()
    private var lines: LineTree = LineTreeSet("", setOf())
    private var playerMove = true

    private lateinit var playerBestOptionView: PlaySettingButton
    private lateinit var playerTheoryOptionView: PlaySettingButton
    private lateinit var playerGambitsOptionView: PlaySettingButton
    private lateinit var playerPreferredOptionView: PlaySettingButton
    private lateinit var opponentBestOptionView: PlaySettingButton
    private lateinit var opponentTheoryOptionView: PlaySettingButton
    private lateinit var opponentGambitsOptionView: PlaySettingButton
    private lateinit var opponentMistakesOptionView: PlaySettingButton

    private val playOptionViews: List<PlaySettingButton>
        get() {
            return listOf(
                playerBestOptionView,
                playerTheoryOptionView,
                playerGambitsOptionView,
                playerPreferredOptionView,
                opponentBestOptionView,
                opponentTheoryOptionView,
                opponentGambitsOptionView,
                opponentMistakesOptionView
            )
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice)
        
        playerBestOptionView = findViewById(R.id.player_best)
        playerTheoryOptionView = findViewById(R.id.player_theory)
        playerGambitsOptionView = findViewById(R.id.player_gambits)
        playerPreferredOptionView = findViewById(R.id.player_preferred)
        opponentBestOptionView = findViewById(R.id.opponent_best)
        opponentTheoryOptionView = findViewById(R.id.opponent_theory)
        opponentGambitsOptionView = findViewById(R.id.opponent_gambit)
        opponentMistakesOptionView = findViewById(R.id.opponent_mistakes)

        for (view in playOptionViews) {
            view.setOnClickListener { view -> togglePlayOption(view as PlaySettingButton) }
        }
        refreshButtonColors()


    }

    private fun togglePlayOption(view: PlaySettingButton) {
        when (view) {
            playerBestOptionView -> playSettings.playerBest = !playSettings.playerBest
            playerTheoryOptionView -> playSettings.playerTheory = !playSettings.playerTheory
            playerGambitsOptionView -> playSettings.playerGambits = !playSettings.playerGambits
            playerPreferredOptionView -> playSettings.playerPreferred = !playSettings.playerPreferred
            opponentBestOptionView -> playSettings.opponentBest = !playSettings.opponentBest
            opponentTheoryOptionView -> playSettings.opponentTheory = !playSettings.opponentTheory
            opponentGambitsOptionView -> playSettings.opponentGambits = !playSettings.opponentGambits
            opponentMistakesOptionView -> playSettings.opponentMistakes = !playSettings.opponentMistakes
        }
        refreshButtonColors()
    }

    private fun refreshButtonColors() {
        for (view in playOptionViews) {
            when (view) {
                playerBestOptionView -> playerBestOptionView.active = playSettings.playerBest
                playerTheoryOptionView -> playerTheoryOptionView.active = playSettings.playerTheory
                playerGambitsOptionView -> playerGambitsOptionView.active = playSettings.playerGambits
                playerPreferredOptionView -> playerPreferredOptionView.active = playSettings.playerPreferred
                opponentBestOptionView -> opponentBestOptionView.active = playSettings.opponentBest
                opponentTheoryOptionView -> opponentTheoryOptionView.active = playSettings.opponentTheory
                opponentGambitsOptionView -> opponentGambitsOptionView.active = playSettings.opponentGambits
                opponentMistakesOptionView -> opponentMistakesOptionView.active = playSettings.opponentMistakes
        }
            view.updateColor()
        }
    }

    // TODO - divide up the moves when the previous move was made
    override fun handleMove(move: Move) {
        val moves = lines.getMoves(boardViewModel.position)
        if (playerMove) {
            val moveResult = determinePlayerMoveResult(moves, move)
            when (moveResult) {
                MoveResult.UNKNOWN -> handleUnknownMove()
                MoveResult.MISTAKE -> handleMistake()
                MoveResult.INCORRECT -> handleIncorrect()
                MoveResult.CORRECT -> handleIncorrect()
                MoveResult.VALID -> handleValid()
            }
        }

    }

    private fun determinePlayerMoveResult(lines: List<LineMove>, move: Move): MoveResult {
        var lineMove: LineMove? = null
        for (line in lines) {
            if (line.move == move) {
                lineMove = line
            }
        }
        if (lineMove == null) return MoveResult.UNKNOWN
        if (lineMove.mistake) return MoveResult.MISTAKE

        if (playSettings.playerBest) {
            if (lineMove.best) return MoveResult.CORRECT
            if (lines.any { it.best }) return MoveResult.INCORRECT
        }
        if (playSettings.playerPreferred) {
            if (lineMove.preferred) return MoveResult.CORRECT
            if (lines.any { it.preferred }) return MoveResult.INCORRECT
        }
        if (playSettings.playerTheory) {
            if (lineMove.preferred) return MoveResult.CORRECT
            if (lines.any { it.preferred }) return MoveResult.INCORRECT
        }
        return MoveResult.VALID
    }

    private fun handleUnknownMove() {
        //TODO
        // Search other books/chapters
        // Offer to add it to a book/chapter
        // Takeback offer
    }

    private fun handleMistake() {
        // TODO show an explanation if available and a takeback
    }

    private fun handleIncorrect() {
        // TODO indicate that there is a specific move or moves to look for based on the settings
    }

    private fun handleCorrect() {
        // TODO indicate if there were other correct moves
    }

    private fun handleValid() {
        // TODO note if there is a best or preferred option
    }

}