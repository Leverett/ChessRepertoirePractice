package com.leverett.chessrepertoirepractice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.leverett.chessrepertoirepractice.ui.views.SquareLayout
import com.leverett.chessrepertoirepractice.utils.CAPTURE_MOVE_SOUND
import com.leverett.chessrepertoirepractice.utils.playSound
import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.basic.piece.isPromotionRank
import com.leverett.rules.chess.representation.*


class BoardFragment : Fragment() {

    private lateinit var squares: Array<Array<SquareLayout>>
    private val viewModel: BoardViewModel = BoardViewModel()

    private val rulesEngine = BasicRulesEngine
    private val position: Position
        get() {
            return viewModel.position
        }
    private var gameStatus: GameStatus = rulesEngine.gameStatus(position)

    val squareDimensions = "1:1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.board_fragment, container, false)
        val boardLayout = view.findViewById<ConstraintLayout>(R.id.board_layout)
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

    fun processMoveSelection(endCoords: Pair<Int, Int>, promotionPiece: PieceEnum? = null) {
        val startCoords = viewModel.activeSquareCoords as Pair<Int,Int>
        val result = findMoveAndStatus(startCoords, endCoords, promotionPiece)
        val status = result.second
        // If it is illegal we just alert but don't change anything
        if (status == MoveStatus.ILLEGAL) {
            squares[endCoords.first][endCoords.second].doIllegalMoveReaction()
        }
        // If it is invalid, we don't do anything, so everything else is dealing with a valid move
        else if (status != MoveStatus.INVALID) {
            val move = result.first!!
            if (move.capture != PieceEnum.EMPTY) {
                playSound(CAPTURE_MOVE_SOUND)
            }
            doMove(move)
        }
    }

    private fun doMove(move: Move) {
        viewModel.position = rulesEngine.getNextPosition(position, move)
        viewModel.activeSquareCoords = null
        updateSquaresToPosition()
        gameStatus = rulesEngine.gameStatus(position)
    }

    private fun updateSquaresToPosition() {
        for (x in 0 until GRID_SIZE) {
            for (y in 0 until GRID_SIZE) {
                squares[x][y].updateSquare()
            }
        }
    }

    private fun findMoveAndStatus(startCoords: Pair<Int,Int>, endCoords: Pair<Int,Int>, promotionPiece: PieceEnum? = null) : Pair<Move?, MoveStatus> {
        //TODO castling
        val startLoc = viewModel.coordsToLoc(startCoords)
        val endLoc = viewModel.coordsToLoc(endCoords)
        return gameStatus.findMoveAndStatus(startLoc, endLoc, promotionPiece)
    }

    fun isValidPromotionMove(startCoords: Pair<Int,Int>, endCoords: Pair<Int,Int>): Boolean {
        if (isPromotionRank(endCoords.second) &&
            viewModel.pieceAtCoords(startCoords).type == PieceEnum.PieceType.PAWN) {
            // This call will find any legal promotion move before any legal one
            val result = findMoveAndStatus(startCoords, endCoords)
            if (result.second == MoveStatus.LEGAL) {
                return true
            }
            // But if there are only illegal promotions, we can signal it before returning false
            if (result.second == MoveStatus.ILLEGAL) {
                squares[endCoords.first][endCoords.second].doIllegalMoveReaction()
            }
        }
        return false
    }

    fun makePromotionPopup(coords: Pair<Int,Int>) {
//        val popupWindow = PopupWindow()
//        val popupView = PromotionPopup(requireContext(), viewModel, coords)
//        popupWindow.showAtLocation()
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
//
//
//        // TODO: Use the ViewModel
//    }


}
