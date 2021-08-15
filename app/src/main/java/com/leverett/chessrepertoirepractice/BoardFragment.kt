package com.leverett.chessrepertoirepractice

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.leverett.chessrepertoirepractice.ui.views.SquareLayout
import com.leverett.chessrepertoirepractice.utils.CAPTURE_MOVE_SOUND
import com.leverett.chessrepertoirepractice.utils.playSound
import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.representation.*


class BoardFragment : Fragment() {

    private lateinit var boardLayout: ConstraintLayout
    private lateinit var squares: Array<Array<SquareLayout>>
    private lateinit var viewModel: BoardViewModel

    private val rulesEngine = BasicRulesEngine
    private val position: Position
        get() {
            return viewModel.position
        }
    private var positionStatus: PositionStatus = rulesEngine.gameStatus(position)

    private val squareDimensions = "1:1"
    private val boardViewModelKey = "boardViewModel"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.board_fragment, container, false)
        boardLayout = view.findViewById(R.id.board_layout)
        val context = requireContext()

//        viewModel = if (savedInstanceState != null) savedInstanceState.get(boardViewModelKey) as BoardViewModel else BoardViewModel()
        viewModel = BoardViewModel()

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

    fun processMoveSelection(endCoords: Pair<Int, Int>, promotionPiece: PieceEnum? = null) {
        val startCoords = viewModel.activeSquareCoords as Pair<Int,Int>
        val result = findMoveAndStatus(startCoords, endCoords, promotionPiece)

        when (result.second) {
            // If it is illegal we just alert but don't change anything
            MoveStatus.ILLEGAL -> {
                squareAt(endCoords).doIllegalMoveReaction()
            }
            MoveStatus.LEGAL -> {
                val move = result.first!!
                if (move.capture != PieceEnum.EMPTY) {
                    playSound(CAPTURE_MOVE_SOUND)
                }
                doMove(move)
            }
            // If it is invalid, we deactivate the active square
            else -> {
                viewModel.activeSquareCoords = null
                squareAt(startCoords).updateSquareColor()
            }
        }
    }

    private fun doMove(move: Move) {
        viewModel.position = rulesEngine.getNextPosition(position, move)
        viewModel.activeSquareCoords = null
        updateSquaresToPosition()
        positionStatus = rulesEngine.gameStatus(position)
    }

    private fun updateSquaresToPosition() {
        for (x in 0 until GRID_SIZE) {
            for (y in 0 until GRID_SIZE) {
                squares[x][y].updateSquare()
            }
        }
    }

    private fun findMoveAndStatus(startCoords: Pair<Int,Int>, endCoords: Pair<Int,Int>, promotionPiece: PieceEnum? = null) : Pair<Move?, MoveStatus> {
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
        configurePromotionSelectionView(popupView.findViewById(R.id.queenPromotionView), PieceEnum.PieceType.QUEEN, endCoords, popupWindow)
        configurePromotionSelectionView(popupView.findViewById(R.id.knightPromotionView), PieceEnum.PieceType.KNIGHT, endCoords, popupWindow)
        configurePromotionSelectionView(popupView.findViewById(R.id.bishopPromotionView), PieceEnum.PieceType.BISHOP, endCoords, popupWindow)
        configurePromotionSelectionView(popupView.findViewById(R.id.rookPromotionView), PieceEnum.PieceType.ROOK, endCoords, popupWindow)

        popupWindow.showAtLocation(boardLayout, Gravity.CENTER, 0, -200)
    }

    private fun configurePromotionSelectionView(view: ImageView, pieceType: PieceEnum.PieceType, endCoords: Pair<Int, Int>, popupWindow: PopupWindow) {
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

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
//
//
//        // TODO: Use the ViewModel
//    }


}
