package com.leverett.chessrepertoirepractice

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.move.LineMove
import com.leverett.repertoire.chess.move.MoveResult
import com.leverett.repertoire.chess.move.MoveResult.*
import com.leverett.repertoire.chess.move.MoveResults
import com.leverett.repertoire.chess.settings.PlaySettings
import com.leverett.rules.chess.representation.MoveAction
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.widget.SwitchCompat
import com.leverett.repertoire.chess.move.MoveDefinition


class PracticeActivity : ChessActivity() {

    override val boardId = R.id.practice_board

    private val repertoireManager = RepertoireManager
    private val playSettings = PlaySettings()

    private var sandboxMode = false
    private var automateOpponent = false

    private val playerMove: Boolean
        get() = boardViewModel.perspectiveColor == boardViewModel.activeColor
    private val latestMove: MoveDefinition?
        get() = if (boardViewModel.gameHistory.currentGameState.moveAction == null) { null } else {
            MoveDefinition(
                boardViewModel.gameHistory.previousGameState()!!.position,
                boardViewModel.gameHistory.currentGameState.position,
                boardViewModel.gameHistory.currentGameState.moveAction!!
            )
        }

    private var opponentMistake = false
    private val mistakesCaught: MutableList<MoveAction> = mutableListOf()

    private val lineMoves: Collection<LineMove>
        get() = repertoireManager.getMoves(boardViewModel.position)

    private lateinit var configurationsView: Spinner
    private lateinit var configurationViewAdapter: ArrayAdapter<String>

    private var playButtonsLayout: Int = R.layout.player_move_buttons
        set(value) {
            field = value
            practiceButtons.removeAllViews()
            layoutInflater.inflate(value, practiceButtons)
            if (playSettings.playerGambits && playerMove) {
                val gambitMoves = lineMoves.filter{it.gambit}
                if (gambitMoves.isNotEmpty()) {
                    val gambitText = makeGambitText(gambitMoves) + displayView.text
                    displayView.text = gambitText
                }
            }
        }
    private lateinit var practiceButtons: ConstraintLayout
    private lateinit var displayView: TextView

    private lateinit var moveResults: MoveResults
    private val previousMoveResults: MoveResults?
        get(){
            val previousGameState = boardViewModel.gameHistory.previousGameState()
            return if (previousGameState == null) null else  MoveResults(repertoireManager.getMoves(previousGameState.position), !playerMove, playSettings)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice)
        practiceButtons = findViewById(R.id.practice_activity_move_buttons)
        displayView = findViewById(R.id.display_view)
        displayView.movementMethod = ScrollingMovementMethod()
        moveResults = MoveResults(lineMoves, playerMove, playSettings)
        setupConfigurationsMenu()
    }

    private fun setupConfigurationsMenu() {
        configurationViewAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, repertoireManager.configurationNames)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        configurationsView = findViewById(R.id.configurations_menu)
        configurationsView.adapter = configurationViewAdapter
        configurationsView.setSelection(configurationViewAdapter.getPosition(repertoireManager.currentConfigurationName))
        configurationsView.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                repertoireManager.loadConfiguration(configurationViewAdapter.getItem(position)!!)
                calculateMoveResults()
            }
        }
    }

    private fun clearText() {
        displayView.text = ""
    }

    private fun playerMovesButtonLayoutId(): Int {
        if (!playSettings.opponentMistakes || latestMove == null || mistakesCaught.contains(
                latestMove!!.moveAction)) {
            return R.layout.player_move_buttons
        }
        return R.layout.player_move_buttons_mistake
    }

    private fun calculateMoveResults() {
        moveResults = MoveResults(lineMoves, playerMove, playSettings)
    }

    private fun calculateUndoMoveResults() {
        moveResults = if (previousMoveResults != null) {
            previousMoveResults!!.copy()
        } else {
            MoveResults(lineMoves, playerMove, playSettings)
        }
    }

    override fun handleMove(moveDefinition: MoveDefinition, undo: Boolean) {
        handleMove(moveDefinition, undo, moveResults)
    }

    private fun handleMove(moveDefinition: MoveDefinition, undo: Boolean, moveResults: MoveResults?) {
        val moveAction = moveDefinition.moveAction
        if (!automateOpponent || !playerMove) {
            clearText()
        }
        boardViewModel.canMove = true
        if (undo) {
            calculateUndoMoveResults()
        }
        var moveResult: MoveResult? = null
        if (sandboxMode) {
            handleSandboxModeMove(moveAction)
        }
        else if (moveResults != null) {
            moveResult = moveResults.getMoveResult(moveDefinition)
            if (playSettings.opponentMistakes && opponentMistake && !undo) {
                handleMissedMistake()
            } else {
                opponentMistake = false
                if (moveResult == null) {
                    handleUnknownMove()
                } else if (!playerMove) { //The underlying board has already been updated, so this is actually responding to a players move
                    opponentMistake = false
                    when (moveResult) {
                        CORRECT -> handleCorrect(moveDefinition, moveResults)
                        VALID -> handlePlayerValid(moveDefinition, moveResults)
                        MISTAKE -> handlePlayerWrongMove(
                            moveDefinition,
                            moveResults,
                            getString(R.string.player_mistake_description_header),
                            true
                        )
                        INCORRECT -> handlePlayerWrongMove(
                            moveDefinition,
                            moveResults,
                            getString(R.string.incorrect_move_description_header),
                            true
                        )
                    }
                } else {
                    opponentMistake = false
                    when (moveResult) {
                        MISTAKE -> handleOpponentMistake(moveAction)
                        // Using the player wrong move here is ok, because it only happens when the player makes the opponent move
                        INCORRECT -> handlePlayerWrongMove(
                            moveDefinition,
                            moveResults,
                            getString(R.string.incorrect_move_description_header),
                            false
                        )
                        else -> handleOpponentValid(moveAction)
                    }
                }
            }
        } else { // This should only be relevant if undo was used back to the starting position
            playButtonsLayout = if (!playerMove) {
                R.layout.opponent_move_buttons
            } else {
                R.layout.player_move_buttons
            }
        }
        calculateMoveResults()
        if (automateOpponent && !playerMove && moveResult != null && (moveResult == CORRECT || moveResult == VALID)) {
            Thread{ doAutomaticMove() }.start()
        }
    }

    private fun doAutomaticMove() = runBlocking {
        launch {
            Thread.sleep(1500)
            runOnUiThread{doOpponentMove()}
        }
    }

    private fun handleSandboxModeMove(moveAction: MoveAction?) {
        playButtonsLayout = R.layout.sandbox_move_buttons
        if (moveAction != null) {
            showDescription()
        }
    }

    private fun handleUnknownMove() {
        playButtonsLayout = R.layout.unknown_move_buttons

        displayView.text = "Unknown move"
    }

    private fun handleCorrect(moveDefinition: MoveDefinition, moveResults: MoveResults) {
        playButtonsLayout = R.layout.opponent_move_buttons
        displayView.text = moveResults.getCorrectMoveDescriptionText(moveDefinition)
    }

    private fun handlePlayerValid(moveDefinition: MoveDefinition, moveResults: MoveResults) {
        playButtonsLayout = R.layout.opponent_move_buttons
        displayView.text = moveResults.getValidMoveDescriptionText(moveDefinition, true)
    }

    private fun handlePlayerWrongMove(moveDefinition: MoveDefinition, moveResults: MoveResults, descriptionHeader: String, disableMoves: Boolean) {
        boardViewModel.canMove = !disableMoves
        playButtonsLayout = R.layout.wrong_move_buttons
        val descriptionText = descriptionHeader + moveResults.getMoveDescriptionText(moveDefinition)
        displayView.text = descriptionText
    }

    private fun handleOpponentValid(moveAction: MoveAction) {
        playButtonsLayout = playerMovesButtonLayoutId()
    }

    private fun handleOpponentMistake(moveAction: MoveAction) {
        opponentMistake = true
        playButtonsLayout = playerMovesButtonLayoutId()
    }

    fun opponentMoveButton(view: View) {
        doOpponentMove()
    }

    private fun doOpponentMove() {
        val move = moveResults.getOpponentMove(playSettings)
        if (move != null) {
            boardFragment.doMove(moveResults.getOpponentMove(playSettings)!!.moveAction)
        }
    }

    fun mistakeAssertionButton(view: View) {
        if (opponentMistake) {
            opponentMistake = false
            mistakesCaught.add(latestMove!!.moveAction)
            playButtonsLayout = playerMovesButtonLayoutId()
            val displayText = getString(R.string.opponent_mistake_description_header) +
                    previousMoveResults!!.getMoveDescriptionText(latestMove!!)
            displayView.text = displayText
        } else {
            mistakesCaught.add(latestMove!!.moveAction)
            displayView.text = getString(R.string.nope)
        }
        playButtonsLayout = playerMovesButtonLayoutId()
    }

    private fun handleMissedMistake() {
        boardViewModel.canMove = false
        playButtonsLayout = R.layout.wrong_move_buttons
        displayView.text = getString(R.string.missed_mistake)
    }

    fun showOptionsButton(view: View) {
        displayView.text = moveResults.getOptionsText()
    }

    fun showDescriptionButton(view: View) {
        showDescription()
    }

    private fun showDescription() {
        if (previousMoveResults != null) {
            displayView.text = previousMoveResults!!.getMoveDescriptionText(latestMove!!)
        }
    }

    fun practiceSettingsButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.practice_settings_popup, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        refreshPlaySettingSwitches(popupView)
        for (switch in popupView.children) {
            if (switch is SwitchCompat) {
                setMoveOptionSwitchOnClick(popupView, switch)
            }
        }
        setupBoardSettingsOptions(popupView)
        popupWindow.showAtLocation(displayView, Gravity.CENTER, 0, 0)
        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            popupWindow.dismiss()
            boardFragment.updateBoardView()
        }
    }

    fun editConfigurationsButton(view: View) {
        val intent = Intent(this, RepertoireActivity::class.java)
        startActivity(intent)
    }

    private fun setMoveOptionSwitchOnClick(popupView: View, switch: SwitchCompat) {
        switch.setOnClickListener {
            when (it) {
                popupView.findViewById<SwitchCompat>(R.id.player_best_switch) ->
                    playSettings.playerBest = !playSettings.playerBest
                popupView.findViewById<SwitchCompat>(R.id.player_theory_switch) ->
                    playSettings.playerTheory = !playSettings.playerTheory
                popupView.findViewById<SwitchCompat>(R.id.player_gambits_switch) ->
                    playSettings.playerGambits = !playSettings.playerGambits
                popupView.findViewById<SwitchCompat>(R.id.player_preferred_switch) ->
                    playSettings.playerPreferred = !playSettings.playerPreferred
                popupView.findViewById<SwitchCompat>(R.id.opponent_best_switch) ->
                    playSettings.opponentBest = !playSettings.opponentBest
                popupView.findViewById<SwitchCompat>(R.id.opponent_theory_switch) ->
                    playSettings.opponentTheory = !playSettings.opponentTheory
                popupView.findViewById<SwitchCompat>(R.id.opponent_gambits_switch) ->
                    playSettings.opponentGambits = !playSettings.opponentGambits
                popupView.findViewById<SwitchCompat>(R.id.opponent_mistakes_switch) ->
                    playSettings.opponentMistakes = !playSettings.opponentMistakes

                popupView.findViewById<Switch>(R.id.sandbox_mode_switch) -> {
                    sandboxMode = switch.isChecked
                    if (sandboxMode) {
                        automateOpponent = false
                    }
                    handleMove(latestMove!!, false, previousMoveResults)
                }
                popupView.findViewById<SwitchCompat>(R.id.automate_opponent_moves_switch) ->
                {
                    automateOpponent = switch.isChecked
                    if (automateOpponent && sandboxMode) {
                        sandboxMode = false
                        handleMove(latestMove!!)
                    }
                }
            }
            calculateMoveResults()
            refreshPlaySettingSwitches(popupView)
        }
    }

    private fun refreshPlaySettingSwitches(popupView: View) {
        popupView.findViewById<SwitchCompat>(R.id.player_best_switch).isChecked = playSettings.playerBest
        popupView.findViewById<SwitchCompat>(R.id.player_theory_switch).isChecked = playSettings.playerTheory
        popupView.findViewById<SwitchCompat>(R.id.player_gambits_switch).isChecked = playSettings.playerGambits
        popupView.findViewById<SwitchCompat>(R.id.player_preferred_switch).isChecked = playSettings.playerPreferred
        popupView.findViewById<SwitchCompat>(R.id.opponent_best_switch).isChecked = playSettings.opponentBest
        popupView.findViewById<SwitchCompat>(R.id.opponent_theory_switch).isChecked = playSettings.opponentTheory
        popupView.findViewById<SwitchCompat>(R.id.opponent_gambits_switch).isChecked = playSettings.opponentGambits
        popupView.findViewById<SwitchCompat>(R.id.opponent_mistakes_switch).isChecked = playSettings.opponentMistakes

        popupView.findViewById<SwitchCompat>(R.id.sandbox_mode_switch).isChecked = sandboxMode
        popupView.findViewById<SwitchCompat>(R.id.automate_opponent_moves_switch).isChecked = automateOpponent
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
        moveResults = MoveResults(lineMoves, playerMove, playSettings)
        playButtonsLayout = if (playerMove) {
            playerMovesButtonLayoutId()
        } else {
            R.layout.opponent_move_buttons
        }
        clearText()
    }

}