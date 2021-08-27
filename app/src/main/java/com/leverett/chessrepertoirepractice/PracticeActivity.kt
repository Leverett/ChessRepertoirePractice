package com.leverett.chessrepertoirepractice

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.leverett.chessrepertoirepractice.ui.views.PlaySettingButton
import com.leverett.chessrepertoirepractice.ui.views.RepertoireListAdapter
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.move.LineMove
import com.leverett.repertoire.chess.move.MoveResult.*
import com.leverett.repertoire.chess.move.MoveResults
import com.leverett.repertoire.chess.settings.PlaySettings
import com.leverett.rules.chess.representation.Move

class PracticeActivity : ChessActivity() {

    override val boardId = R.id.practice_board

    private val repertoireManager = RepertoireManager
    private val playSettings: PlaySettings
        get() = repertoireManager.playSettings

    private val playerMove: Boolean
        get() = boardViewModel.perspectiveColor == boardViewModel.activeColor
    private val latestMove: Move
        get() = boardViewModel.gameHistory.currentGameState.move!!

    private var opponentMistake = false
    private val mistakesCaught: MutableList<Move> = mutableListOf()

    private val lineMoves: Collection<LineMove>
        get() = repertoireManager.getMoves(boardViewModel.position)

    private var playButtonsLayout: Int = playerMovesButtonLayoutId()
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
    private lateinit var practiceButtons: ConstraintLayout
    private lateinit var displayView: TextView

    private lateinit var moveResults: MoveResults
    private val previousMoveResults: MoveResults?
        get(){
            val previousGameState = boardViewModel.gameHistory.previousGameState()
            return if (previousGameState == null) null else  MoveResults(repertoireManager.getMoves(previousGameState!!.position), playSettings, !playerMove)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice)
        practiceButtons = findViewById(R.id.practice_activity_move_buttons)
        displayView = findViewById(R.id.display_view)
        moveResults = MoveResults(lineMoves, playSettings, playerMove)
        playButtonsLayout = playerMovesButtonLayoutId()
    }

    private fun clearText() {
        displayView.text = ""
    }

    private fun playerMovesButtonLayoutId(): Int {
        if (!playSettings.opponentMistakes || mistakesCaught.contains(latestMove)) {
            return R.layout.player_move_buttons
        }
        return R.layout.player_move_buttons_mistake
    }

    private fun calculateMoveResults() {
        moveResults = MoveResults(lineMoves, playSettings, playerMove)
    }

    private fun calculateUndoMoveResults() {
        moveResults = previousMoveResults!!.copy()
    }

    override fun handleMove(move: Move?, undo: Boolean) {
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
        playButtonsLayout = R.layout.unknown_move_buttons
        displayView.text = "Unknown move"
    }

    private fun handleCorrect(move: Move) {
        playButtonsLayout = R.layout.opponent_move_buttons
        displayView.text = moveResults.getCorrectMoveDescriptionText(move)
    }

    private fun handlePlayerValid(move: Move) {
        playButtonsLayout = R.layout.opponent_move_buttons
        displayView.text = moveResults.getValidMoveDescriptionText(move, true)
    }

    private fun handlePlayerWrongMove(move: Move, descriptionHeader: String, disableMoves: Boolean) {
        boardViewModel.canMove = !disableMoves
        playButtonsLayout = R.layout.wrong_move_button
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
        playButtonsLayout = R.layout.wrong_move_button
        displayView.text = getString(R.string.missed_mistake)
    }

    fun showDescriptionButton(view: View) {
        if (previousMoveResults != null) {
            displayView.text = previousMoveResults!!.getMoveDescriptionText(latestMove)
        }
    }

    fun showOptionsButton(view: View) {
        displayView.text = moveResults.getOptionsText()
    }

    fun editDescriptionButton(view: View) {
        // TODO update the description box
    }

    fun addToRepertoireButton(view: View){
        // TODO this (some sort of popup)
    }

    fun searchRepertoireButton(view: View) {
        // TODO some sort of popup/use the text view, and offer a way to add to the current repertoire
    }

    fun repertoireSettingsButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.repertoire_settings_popup, null) as ConstraintLayout
        val adapter = RepertoireListAdapter()
        val repertoireListView = popupView.findViewById<ExpandableListView>(R.id.repertoire_list_view)
        repertoireListView.setAdapter(adapter)
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(displayView, Gravity.CENTER, 0, 0)
        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            popupWindow.dismiss()
        }
    }

    fun moveSettingsButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.move_settings_popup, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        refreshPlaySettingSwitches(popupView)
        for (view in popupView.children) {
            if (view is Switch) {
                setMoveOptionSwitchOnClick(popupView, view)
            }
        }
        popupWindow.showAtLocation(displayView, Gravity.CENTER, 0, 0)

        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun setMoveOptionSwitchOnClick(popupView: View, switch: Switch) {
        switch.setOnClickListener {
            when (it) {
                popupView.findViewById<Switch>(R.id.player_best_switch) ->
                    playSettings.playerBest = !playSettings.playerBest
                popupView.findViewById<Switch>(R.id.player_theory_switch) ->
                    playSettings.playerTheory = !playSettings.playerTheory
                popupView.findViewById<Switch>(R.id.player_gambits_switch) ->
                    playSettings.playerGambits = !playSettings.playerGambits
                popupView.findViewById<Switch>(R.id.player_preferred_switch) ->
                    playSettings.playerPreferred = !playSettings.playerPreferred
                popupView.findViewById<Switch>(R.id.opponent_best_switch) ->
                    playSettings.opponentBest = !playSettings.opponentBest
                popupView.findViewById<Switch>(R.id.opponent_theory_switch) ->
                    playSettings.opponentTheory = !playSettings.opponentTheory
                popupView.findViewById<Switch>(R.id.opponent_gambits_switch) ->
                    playSettings.opponentGambits = !playSettings.opponentGambits
                popupView.findViewById<Switch>(R.id.opponent_mistakes_switch) ->
                    playSettings.opponentMistakes = !playSettings.opponentMistakes
            }
            calculateMoveResults()
            refreshPlaySettingSwitches(popupView)
        }
    }

    private fun refreshPlaySettingSwitches(popupView: View) {
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

    override fun resetActivity() {
        moveResults = MoveResults(lineMoves, playSettings, playerMove)
        playButtonsLayout = playerMovesButtonLayoutId()
        clearText()
    }

}