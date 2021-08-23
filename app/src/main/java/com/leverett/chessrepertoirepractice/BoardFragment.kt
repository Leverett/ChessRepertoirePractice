package com.leverett.chessrepertoirepractice

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.leverett.chessrepertoirepractice.ui.views.SquareLayout
import com.leverett.chessrepertoirepractice.utils.BoardStyle
import com.leverett.chessrepertoirepractice.utils.CAPTURE_MOVE_SOUND
import com.leverett.chessrepertoirepractice.utils.PieceStyle
import com.leverett.chessrepertoirepractice.utils.playSound
import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.parsing.PGNBuilder
import com.leverett.rules.chess.representation.*


class BoardFragment(var viewModel: BoardViewModel = BoardViewModel()) : Fragment() {

    private val squareDimensions = "1:1"

    private lateinit var boardLayout: ConstraintLayout
    private lateinit var squares: Array<Array<SquareLayout>>
    private lateinit var historyView: TextView

    private val rulesEngine = BasicRulesEngine

    private val position: Position
        get() = viewModel.position
    private val positionStatus: PositionStatus
        get() = viewModel.positionStatus

    private val gameHistory: GameHistory
        get() = viewModel.gameHistory

    private val activity: ChessActivity
        get() = getActivity() as ChessActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.board_fragment, container, false)
        boardLayout = view.findViewById(R.id.grid_layout)
        historyView = view.findViewById(R.id.move_history)
        getUISettings()
        val context = requireContext()

        squares = Array(GRID_SIZE) {x -> Array(GRID_SIZE) {y -> SquareLayout(context, viewModel, x, y).also{boardLayout.addView(it)}} }

        for (x in 0 until GRID_SIZE) {
            for (y in 0 until GRID_SIZE) {
                val square = squares[x][y]
                val layoutParams = ConstraintLayout.LayoutParams(0, 0)
                layoutParams.dimensionRatio = squareDimensions
                if (x == 0) {
                    layoutParams.leftToLeft = boardLayout.id
                } else {
                    layoutParams.leftToRight = squares[x-1][y].id
                }
                if (x == GRID_SIZE - 1) {
                    layoutParams.rightToRight = boardLayout.id
                } else {
                    layoutParams.rightToLeft = squares[x+1][y].id
                }
                if (y == 0) {
                    layoutParams.bottomToBottom = boardLayout.id
                } else {
                    layoutParams.bottomToTop = squares[x][y-1].id
                }
                if (y == GRID_SIZE - 1) {
                    layoutParams.topToTop = boardLayout.id
                } else {
                    layoutParams.topToBottom = squares[x][y+1].id
                }
                square.layoutParams = layoutParams
            }
        }
        return view
    }

    private fun getUISettings() {
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return

        val defaultBoardStyle = viewModel.boardStyle.name
        val boardStylePrefKey = getString(R.string.board_style_pref_key)
        val boardStyleVal = sharedPref.getString(boardStylePrefKey, defaultBoardStyle)
        if (boardStyleVal != null) {
            viewModel.boardStyle = BoardStyle.valueOf(boardStyleVal)
        }
        val defaultPieceStyle = viewModel.pieceStyle.name
        val pieceStylePrefKey = getString(R.string.piece_style_pref_key)
        val pieceStyleVal = sharedPref.getString(pieceStylePrefKey, defaultPieceStyle)
        if (pieceStyleVal != null) {
            viewModel.pieceStyle = PieceStyle.valueOf(pieceStyleVal)
        }
    }

    fun processMoveSelection(endCoords: Pair<Int, Int>, promotionPiece: Piece? = null) {
        val startCoords = viewModel.activeSquareCoords as Pair<Int,Int>
        val result = findMoveAndStatus(startCoords, endCoords, promotionPiece)

        when (result.second) {
            // If it is invalid, we deactivate the active square
            MoveStatus.INVALID ->  {
                viewModel.activeSquareCoords = null
                squareAt(startCoords).updateSquareColor()
            }
            // If it is illegal we just alert but don't change anything
            MoveStatus.ILLEGAL -> {
                squareAt(endCoords).doIllegalMoveReaction()
            }
            MoveStatus.LEGAL -> {
                val move = result.first!!
                if (move.capture != Piece.EMPTY) {
                    playSound(CAPTURE_MOVE_SOUND)
                }
                doMove(move)
            }
        }
    }

    fun doMove(move: Move) {
        var nextGameState = gameHistory.nextGameState()
        if (nextGameState == null || nextGameState.move != move) {
            val pgnBuilder = PGNBuilder
            val nextPosition = rulesEngine.getNextPosition(position, move)
            val nextPositionStatus = rulesEngine.positionStatus(nextPosition)
            nextGameState = GameState(nextPosition, nextPositionStatus, move, pgnBuilder.makeMoveNotation(position, move))
            gameHistory.addGameState(nextGameState)
        }
        setGameState(nextGameState)
    }

    fun redoNextMove() {
        val nextGameState = gameHistory.nextGameState()
        if (nextGameState != null) {
            setGameState(nextGameState)
        }
    }

    fun undoMove() {
        val previousGameState = gameHistory.previousGameState()
        if (previousGameState != null) {
            setGameState(previousGameState, true)
        }
    }

    private fun setGameState(gameState: GameState, undo: Boolean = false) {
        gameHistory.currentGameState = gameState
        viewModel.activeSquareCoords = null
        updateBoardView()
        activity.handleMove(gameState.move, undo)
    }

    fun updateBoardView() {
        for (x in 0 until GRID_SIZE) {
            for (y in 0 until GRID_SIZE) {
                squares[x][y].updateSquare()
            }
        }
        historyView.text = viewModel.gameHistory.stringToNow()
    }

    private fun findMoveAndStatus(startCoords: Pair<Int,Int>, endCoords: Pair<Int,Int>, promotionPiece: Piece? = null) : Pair<Move?, MoveStatus> {
        val startLoc = viewModel.coordsToLoc(startCoords)
        val endLoc = viewModel.coordsToLoc(endCoords)
        return positionStatus.findMoveAndStatus(startLoc, endLoc, promotionPiece)
    }

    fun isValidPromotionMove(startCoords: Pair<Int,Int>, endCoords: Pair<Int,Int>): Boolean {
        if (rulesEngine.isMovePromotion(viewModel.position, viewModel.coordsToLoc(startCoords), viewModel.coordsToLoc(endCoords))) {
            // This call will find any legal promotion move before any legal one
            val result = findMoveAndStatus(startCoords, endCoords)
            if (result.second == MoveStatus.LEGAL) {
                return true
            }
            // But if there are only illegal promotions, we can signal it before returning false
            if (result.second == MoveStatus.ILLEGAL) {
                squareAt(endCoords).doIllegalMoveReaction()
            }
        }
        return false
    }

    fun makePromotionPopup(endCoords: Pair<Int, Int>) {
        val popupView = layoutInflater.inflate(R.layout.promotion_popup_layout, null)
        popupView.setBackgroundColor(viewModel.boardStyle.promotionBackground)

        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        configurePromotionSelectionView(popupView.findViewById(R.id.queenPromotionView), Piece.PieceType.QUEEN, endCoords, popupWindow)
        configurePromotionSelectionView(popupView.findViewById(R.id.knightPromotionView), Piece.PieceType.KNIGHT, endCoords, popupWindow)
        configurePromotionSelectionView(popupView.findViewById(R.id.bishopPromotionView), Piece.PieceType.BISHOP, endCoords, popupWindow)
        configurePromotionSelectionView(popupView.findViewById(R.id.rookPromotionView), Piece.PieceType.ROOK, endCoords, popupWindow)

        popupWindow.showAsDropDown(squareAt(Pair(GRID_SIZE/2, GRID_SIZE -1)), Gravity.CENTER, 0, -200)
    }

    private fun configurePromotionSelectionView(view: ImageView, pieceType: Piece.PieceType, endCoords: Pair<Int, Int>, popupWindow: PopupWindow) {
        val piece = getPiece(viewModel.activeColor, pieceType)
        view.setImageResource(viewModel.pieceStyle.getPieceImageResource(piece)!!)
        view.setOnClickListener {
            processMoveSelection(endCoords, piece)
            popupWindow.dismiss()
        }

    }

    fun squareAt(coords: Pair<Int, Int>): SquareLayout {
        return squares[coords.first][coords.second]
    }

    fun switchPerspective(view: View) {
        val perspectiveSwitch = view as SwitchCompat
        perspectiveSwitch.isChecked = perspectiveSwitch.isChecked
        viewModel.perspectiveColor = !perspectiveSwitch.isChecked
        updateBoardView()
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
//
//
//        // TODO: Use the ViewModel
//    }


}
