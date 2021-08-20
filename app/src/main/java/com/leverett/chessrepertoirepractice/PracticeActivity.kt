package com.leverett.chessrepertoirepractice

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.leverett.chessrepertoirepractice.ui.views.PlaySettingButton
import com.leverett.chessrepertoirepractice.utils.BoardStyle
import com.leverett.chessrepertoirepractice.utils.PieceStyle
import com.leverett.repertoire.chess.PGNParser
import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.LineMove
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
            "1. d4 \$THEORY {just a comment} d5 {comment for a hint} 2. c4 dxc4 \$MISTAKE 3. e3 (3. e4 \$MISTAKE) 3... b5 4. Qf3 *\n" +
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

    private var playSettings = PlaySettings()
    private val pgnParser = PGNParser
    private val pgnBuilder = PGNBuilder
    private var lines: Book = pgnParser.parseAnnotatedPgnToBook(bookExample) //LineTree = LineTreeSet("", setOf())
    private val playerMove: Boolean
        get() = boardViewModel.perspectiveColor == boardViewModel.activeColor
    private val latestMove: Move
        get() = boardViewModel.gameHistory.currentGameState.move!!

    private var opponentMistake = false
    private val mistakesCaught: MutableList<Move> = mutableListOf()

    private val lineMoves: Collection<LineMove>
        get() = lines.getMoves(boardViewModel.position)

    private var playButtonsLayout: Int = R.layout.player_move_buttons_layout
        set(value) {
            field = value
            practiceButtons.removeAllViews()
            layoutInflater.inflate(value, practiceButtons)
            if (playSettings.playerGambits && playerMove) {
                val gambitMoves = lineMoves.filter{it.gambit}
                if (gambitMoves.isNotEmpty()) {
                    displayView.text = makeGambitText(gambitMoves) + displayView.text
                }
            }
        }

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
    private val previousMoveResults: MoveResults?
        get(){
            val previousGameState = boardViewModel.gameHistory.previousGameState()
            return if (previousGameState == null) null else  MoveResults(lines.getMoves(previousGameState!!.position), playSettings, !playerMove)
        }
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
        practiceButtons = findViewById(R.id.practice_activity_move_buttons)
        displayView = findViewById(R.id.display_view)

        for (view in playOptionViews) {
            view.setOnClickListener { view -> togglePlayOption(view as PlaySettingButton) }
        }
        refreshPlayOptionButtonColors()
        moveResults = MoveResults(lineMoves, playSettings, playerMove)
        playButtonsLayout = playerMovesButtonLayoutId()

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

    private fun playerMovesButtonLayoutId(): Int {
        if (!playSettings.opponentMistakes || mistakesCaught.contains(latestMove)) {
            return R.layout.player_move_buttons_layout
        }
        return R.layout.player_move_buttons_mistake_layout
    }

    private fun calculateMoveResults() {
        moveResults = MoveResults(lineMoves, playSettings, playerMove)
    }

    private fun calculateUndoMoveResults() {
        moveResults = previousMoveResults!!.copy()
    }

    override fun handleMove(move: Move?, undo: Boolean) {
//            Log.e("handleMove","moveResult: $moveResult")
//            Log.e("handleMove","lines: " + lines.quickDisplay())
//        Log.e("handleMove","move: $move")
//        Log.e("handleMove",
//            "moves: " + lineMoves.joinToString(",") {it.algMove})
//            Log.e("handleMove","nextMoveResults: " + moveResults.quickDisplay())
        clearText()
        boardViewModel.canMove = true
        if (move != null) {
            if (undo) {
                calculateUndoMoveResults()
            }
            val moveResult = moveResults.getMoveResult(move)
            if (playSettings.opponentMistakes && opponentMistake && !undo) {
                handleMissedMistake()
            } else {
                opponentMistake = false
                if (moveResult == null) {
                    handleUnknownMove()
                }
                else if (!playerMove) { //The underlying board has already been updated, so this is actually responding to a players move
                    opponentMistake = false
                    when (moveResult) {
                        CORRECT -> handleCorrect(move)
                        VALID -> handlePlayerValid(move)
                        MISTAKE -> handlePlayerWrongMove(move, getString(R.string.player_mistake_description_header), true)
                        INCORRECT -> handlePlayerWrongMove(move, getString(R.string.incorrect_move_description_header), true)
                    }
                }
                else {
                    opponentMistake = false
                    when (moveResult) {
                        MISTAKE -> handleOpponentMistake(move)
                        // Using the player wrong move here is ok, because it only happens when the player makes the opponent move
                        INCORRECT -> handlePlayerWrongMove(move, getString(R.string.incorrect_move_description_header), false)
                        else -> handleOpponentValid(move)
                    }
                }
            }
        }
        calculateMoveResults()
    }

    private fun handleUnknownMove() {
        boardViewModel.canMove = false
        playButtonsLayout = R.layout.unknown_move_buttons_layout
        displayView.text = "Unknown move"
    }

    private fun handleCorrect(move: Move) {
        playButtonsLayout = R.layout.opponent_move_buttons_layout
        displayView.text = moveResults.getCorrectMoveDescriptionText(move)
    }

    private fun handlePlayerValid(move: Move) {
        playButtonsLayout = R.layout.opponent_move_buttons_layout
        displayView.text = moveResults.getValidMoveDescriptionText(move, true)
    }

    private fun handlePlayerWrongMove(move: Move, descriptionHeader: String, disableMoves: Boolean) {
        boardViewModel.canMove = !disableMoves
        playButtonsLayout = R.layout.wrong_move_button_layout
        displayView.text = descriptionHeader + moveResults.getMoveDescriptionText(move)
    }

    private fun handleOpponentValid(move: Move) {
        playButtonsLayout = playerMovesButtonLayoutId()
    }

    private fun handleOpponentMistake(move: Move) {
        // TODO find a way to prevent having to redo this when undoing backwards past it
        opponentMistake = true
        playButtonsLayout = playerMovesButtonLayoutId()
    }


    fun opponentMoveButton(view: View) {
        boardFragment.doMove(moveResults.getOpponentMove(playSettings))
    }

    fun mistakeAssertionButton(view: View) {
        if (opponentMistake) {
            opponentMistake = false
            mistakesCaught.add(latestMove!!)
            playButtonsLayout = playerMovesButtonLayoutId()
            displayView.text = getString(R.string.opponent_mistake_description_header) +
                    previousMoveResults!!.getMoveDescriptionText(latestMove)
        } else {
            mistakesCaught.add(latestMove)
            displayView.text = getString(R.string.nope)
        }
        playButtonsLayout = playerMovesButtonLayoutId()
    }

    private fun handleMissedMistake() {
        boardViewModel.canMove = false
        playButtonsLayout = R.layout.wrong_move_button_layout
        displayView.text = getString(R.string.missed_mistake)
    }

    fun addToRepertoireButton(view: View){
        // TODO this (some sort of popup)
    }

    fun searchRepertoireButton(view: View) {
        // TODO some sort of popup/use the text view, and offer a way to add to the current repertoire
    }

    fun showDescriptionButton(view: View) {
        if (previousMoveResults != null) {
            displayView.text = previousMoveResults!!.getMoveDescriptionText(latestMove)
        }
    }

    fun showHintButton(view: View) {
        // update the description box
    }

    fun showOptionsButton(view: View) {
        displayView.text = moveResults.getOptionsText()
    }

    fun boardSettingsButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.board_settings_popup_layout, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        val boardStyleSpinner = popupView.findViewById(R.id.board_style_spinner) as Spinner
        val boardStyleSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, BoardStyle.values())
        boardStyleSpinner.setSelection(boardStyleSpinnerAdapter.getPosition(boardViewModel.boardStyle))
        val pieceStyleSpinner = popupView.findViewById(R.id.piece_style_spinner) as Spinner
        val pieceStyleSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, PieceStyle.values())
        pieceStyleSpinner.setSelection(pieceStyleSpinnerAdapter.getPosition(boardViewModel.pieceStyle))
        popupWindow.showAtLocation(displayView, Gravity.CENTER, 0, 0)

        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            boardViewModel.boardStyle = boardStyleSpinner.selectedItem as BoardStyle
            boardViewModel.pieceStyle = pieceStyleSpinner.selectedItem as PieceStyle
            popupWindow.dismiss()
        }
    }

    fun moveSettingsButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.move_settings_popup_layout, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        val newPlaySettings = playSettings.copy()
        refreshPlaySettingSwitches(popupView, newPlaySettings)
        for (view in popupView.children) {
            if (view is Switch) {
                setMoveOptionSwitchOnClick(popupView, view, newPlaySettings)
            }
        }
        popupWindow.showAtLocation(displayView, Gravity.CENTER, 0, 0)

        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            moveSettingsOkButton(newPlaySettings)
            popupWindow.dismiss()
        }

        popupView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun setMoveOptionSwitchOnClick(popupView: View, switch: Switch, newPlaySettings: PlaySettings) {
        switch.setOnClickListener {
            when (it) {
                popupView.findViewById<Switch>(R.id.player_best_switch) ->
                    newPlaySettings.playerBest = !newPlaySettings.playerBest
                popupView.findViewById<Switch>(R.id.player_theory_switch) ->
                    newPlaySettings.playerTheory = !newPlaySettings.playerTheory
                popupView.findViewById<Switch>(R.id.player_gambits_switch) ->
                    newPlaySettings.playerGambits = !newPlaySettings.playerGambits
                popupView.findViewById<Switch>(R.id.player_preferred_switch) ->
                    newPlaySettings.playerPreferred = !newPlaySettings.playerPreferred
                popupView.findViewById<Switch>(R.id.opponent_best_switch) ->
                    newPlaySettings.opponentBest = !newPlaySettings.opponentBest
                popupView.findViewById<Switch>(R.id.opponent_theory_switch) ->
                    newPlaySettings.opponentTheory = !newPlaySettings.opponentTheory
                popupView.findViewById<Switch>(R.id.opponent_gambits_switch) ->
                    newPlaySettings.opponentGambits = !newPlaySettings.opponentGambits
                popupView.findViewById<Switch>(R.id.opponent_mistakes_switch) ->
                    newPlaySettings.opponentMistakes = !newPlaySettings.opponentMistakes
            }
            refreshPlaySettingSwitches(popupView, newPlaySettings)
        }

    }

    fun moveSettingsOkButton(newPlaySettings: PlaySettings) {
        playSettings = newPlaySettings
        calculateMoveResults()
        refreshPlayOptionButtonColors()
    }

    private fun refreshPlaySettingSwitches(popupView: View, playSettings: PlaySettings) {
        popupView.findViewById<Switch>(R.id.player_best_switch).isChecked = playSettings.playerBest
        popupView.findViewById<Switch>(R.id.player_theory_switch).isChecked = playSettings.playerTheory
        popupView.findViewById<Switch>(R.id.player_gambits_switch).isChecked = playSettings.playerGambits
        popupView.findViewById<Switch>(R.id.player_preferred_switch).isChecked = playSettings.playerPreferred
        popupView.findViewById<Switch>(R.id.opponent_best_switch).isChecked = playSettings.opponentBest
        popupView.findViewById<Switch>(R.id.opponent_theory_switch).isChecked = playSettings.opponentTheory
        popupView.findViewById<Switch>(R.id.opponent_gambits_switch).isChecked = playSettings.opponentGambits
        popupView.findViewById<Switch>(R.id.opponent_mistakes_switch).isChecked = playSettings.opponentMistakes
    }

    private fun makeGambitText(gambitLineMoves: List<LineMove>) : String {
        val gambitMoves = mutableSetOf<String>()
        gambitLineMoves.forEach{gambitMoves.add(it.algMove)}
        if (gambitMoves.isNotEmpty()) {
            val plural = if (gambitMoves.size > 1) "s" else ""
            return gambitMoves.joinToString(", ", "Available gambit$plural: ")
        }
        return ""
    }

}