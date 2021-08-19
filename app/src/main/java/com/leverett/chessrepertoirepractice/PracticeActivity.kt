package com.leverett.chessrepertoirepractice

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.leverett.chessrepertoirepractice.ui.views.PlaySettingButton
import com.leverett.repertoire.chess.PGNParser
import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.LineMove
import com.leverett.repertoire.chess.lines.LineTree
import com.leverett.repertoire.chess.lines.LineTreeSet
import com.leverett.repertoire.chess.mode.MoveResult.*
import com.leverett.repertoire.chess.mode.MoveResults
import com.leverett.repertoire.chess.mode.PlaySettings
import com.leverett.rules.chess.parsing.PGNBuilder
import com.leverett.rules.chess.representation.Move

class PracticeActivity : ChessActivity() {

    private val bookExample = "[Event \"Test Study: Chapter 1\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/32qGstHX\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:15:10\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D20\"]\n" +
            "[Opening \"Queen's Gambit Accepted: Old Variation\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. d4 \$THEORY {just a comment} d5 2. c4 dxc4 3. e3 (3. e4) 3... b5 4. Qf3 *\n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 2\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/C8nP4WlO\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:33:53\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D30\"]\n" +
            "[Opening \"Queen's Gambit Declined\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. d4 d5 2. c4 e6 *\n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 2\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/C8nP4WlO\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:33:53\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D30\"]\n" +
            "[Opening \"Queen's Gambit Declined\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. e4 \$THEORY d5 2. c4 e6 *"

    override val boardId = R.id.practice_board

    private val playSettings = PlaySettings()
    private val pgnParser = PGNParser
    private val pgnBuilder = PGNBuilder
    private var lines: Book = pgnParser.parseAnnotatedPgnToBook(bookExample) //LineTree = LineTreeSet("", setOf())
    private val playerMove: Boolean
        get() = boardViewModel.perspectiveColor == boardViewModel.activeColor

    private val lineMoves: Collection<LineMove>
        get() = lines.getMoves(boardViewModel.position)

    private lateinit var playerBestOptionView: PlaySettingButton
    private lateinit var playerTheoryOptionView: PlaySettingButton
    private lateinit var playerGambitsOptionView: PlaySettingButton
    private lateinit var playerPreferredOptionView: PlaySettingButton
    private lateinit var opponentBestOptionView: PlaySettingButton
    private lateinit var opponentTheoryOptionView: PlaySettingButton
    private lateinit var opponentGambitsOptionView: PlaySettingButton
    private lateinit var opponentMistakesOptionView: PlaySettingButton
    private lateinit var practiceButtons: ConstraintLayout
    private lateinit var displayView: TextView

//    private var nextMoveResultsO: MutableMap<Move, MoveResult> = mutableMapOf()
    private lateinit var moveResults: MoveResults

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
        practiceButtons = findViewById(R.id.practice_activity_buttons)
        displayView = findViewById(R.id.display_view)

        for (view in playOptionViews) {
            view.setOnClickListener { view -> togglePlayOption(view as PlaySettingButton) }
        }
        refreshPlayOptionButtonColors()
        calculateMoveResults()

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
        refreshPlayOptionButtonColors()
        calculateMoveResults()
    }

    private fun refreshPlayOptionButtonColors() {
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

    private fun clearText() {
        displayView.text = ""
    }

    private fun setButtonsLayout(id: Int) {
        practiceButtons.removeAllViews()
        layoutInflater.inflate(id, practiceButtons)
    }

    private fun calculateMoveResults() {
        moveResults = MoveResults(lineMoves, playSettings, playerMove)
    }

    override fun handleMove(move: Move?) {
        clearText()
        if (move != null) {
            val moveResult = moveResults.getMoveResult(move)
            //TODO get rid of this
//            Log.e("handleMove","moveResult: $moveResult")
//            Log.e("handleMove","lines: " + lines.quickDisplay())
//            Log.e("handleMove",
//                "moves: " + lineMoves.joinToString(",") {it.algMove})
//            Log.e("handleMove","nextMoveResults: " + moveResults.quickDisplay())
            if (moveResult == null) {
                handleUnknownMove()
            } else if (!playerMove) { //The underlying board has already been updated
                when (moveResult) {
                    MISTAKE -> handleMistake(move)
                    INCORRECT -> handleIncorrect(move)
                    CORRECT -> handleCorrect(move)
                    VALID -> handleValid(move)
                }
            }
//        if (playerMove) {
//            val moveResult = determinePlayerMoveResult(lineMoves, move)
//            when (nextMoveResults[moveResult]) {
//                UNKNOWN -> handleUnknownMove()
//                MISTAKE -> handleMistake()
//                INCORRECT -> handleIncorrect()
//                CORRECT -> handleCorrect()
//                VALID -> handleValid()
//            }
//        }
        }
        else {
            practiceButtons.removeAllViews()
            layoutInflater.inflate(R.layout.player_move_buttons_layout, practiceButtons)
        }
        moveResults = MoveResults(lineMoves, playSettings, playerMove)
    }

    private fun handleUnknownMove() {
        boardViewModel.canMove = false
        setButtonsLayout(R.layout.unknown_move_buttons_layout)
        displayView.text = "Unknown move"
    }

    private fun handleCorrect(move: Move) {
        displayView.text = moveResults.getCorrectMoveDescriptionText(move)
        setButtonsLayout(R.layout.opponent_move_buttons_layout)
        // TODO opponent move
    }

    private fun handleMistake(move: Move) {
        // TODO show an explanation if available and a takeback
    }

    private fun handleIncorrect(move: Move) {
        // TODO indicate that there is a specific move or moves to look for based on the settings
    }

    private fun handleValid(move: Move) {
        displayView.text = moveResults.getValidMoveDescriptionText(move)
        setButtonsLayout(R.layout.opponent_move_buttons_layout)
        // TODO note if there is a best or preferred option
    }

    fun addToRepertoireButton(view: View){
        // TODO this (some sort of popup)
    }

    fun searchRepertoireButton(view: View) {
        // TODO some sort of popup/use the text view, and offer a way to add to the current repertoire
    }

    fun showDescriptionButton(view: View) {
        // update the description box
    }

    fun showHintButton(view: View) {
        // update the description box
    }

    fun showOptionsButton(view: View) {

        // update the description box
    }

}