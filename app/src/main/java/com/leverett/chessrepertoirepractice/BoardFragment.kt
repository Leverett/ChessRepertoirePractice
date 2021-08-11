package com.leverett.chessrepertoirepractice

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.leverett.rules.chess.basic.piece.Pawn
import com.leverett.chessrepertoirepractice.ui.views.SquareLayout
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.MoveStatus
import com.leverett.rules.chess.representation.PieceEnum.EMPTY
import com.leverett.rules.chess.representation.Position.Companion.GRID_SIZE


class BoardFragment() : Fragment() {

    companion object {
        fun newInstance() = BoardFragment()
    }

    private lateinit var viewModel: BoardViewModel

    private lateinit var squares: Array<Array<SquareLayout>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("BoardFragment","HEREEEE")
        val view: View = inflater.inflate(R.layout.board_fragment, container, false)
        val boardLayout = view.findViewById<ConstraintLayout>(R.id.board_layout)
        val context = requireContext()
        Log.e("BoardFragment","HEREEEE2")
        val tem = BoardViewModel()
        Log.e("BoardFragment","HEREEEE22")
        viewModel = BoardViewModel()

        Log.e("BoardFragment","HEREEEE3")
        squares = Array(GRID_SIZE) {i -> Array(GRID_SIZE) {j -> SquareLayout(context, viewModel, i, j).also{boardLayout.addView(it)}} }
        Log.e("BoardFragment","HEREEEE4")

        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {

                val square = squares[i][j]
                val layoutParams = ConstraintLayout.LayoutParams(0, 0)
                layoutParams.dimensionRatio = "1:1"
                if (i == 0) {
                    layoutParams.leftToLeft = boardLayout.id
                } else {
                    layoutParams.leftToRight = squares[i-1][j].id
                }
                if (i == GRID_SIZE - 1) {
                    layoutParams.rightToRight = boardLayout.id
                } else {
                    layoutParams.rightToLeft = squares[i+1][j].id
                }
                if (j == 0) {
                    layoutParams.bottomToBottom = boardLayout.id
                } else {
                    layoutParams.bottomToTop = squares[i][j-1].id
                }
                if (j == GRID_SIZE - 1) {
                    layoutParams.topToTop = boardLayout.id
                } else {
                    layoutParams.topToBottom = squares[i][j+1].id
                }
                square.layoutParams = layoutParams
            }
//            setOnClickListener(boardLayout)
//            setOnDragListener(boardLayout)
        }
        Log.e("BoardFragment","HEREEEE5")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BoardViewModel::class.java)


        // TODO: Use the ViewModel
    }

    private fun setOnClickListener(board: ConstraintLayout) {
        board.setOnClickListener { view ->
            if (view is SquareLayout) {
                val activeSquareCoords = viewModel.activeSquareCoords
                if (activeSquareCoords != null) {
                    val targetCoords = view.coords
                    if (targetCoords == activeSquareCoords &&
                        view.activeSquare
                    ) { // TODO This should be a redundant check
                        viewModel.activeSquareCoords = null
                        view.activeSquare = false
                    } else if (Pawn.isPromotionRank(targetCoords.second)) {
                        makePromotionDialog(targetCoords) //TODO this
                        //TODO also preempt this if no promotion is a legal move
                    } else {
                        makeAndDoMove(activeSquareCoords, targetCoords)

                    }
                }
            }
        }
    }
    private fun setOnDragListener(board: ConstraintLayout) {
        //TODO dragging
//        board.setOnDragListener{ view, event ->
//            if (view is SquareLayout) {
//                val startCoords = view.coords
//            }
//            false
//        }
    }

    private fun makeAndDoMove(startCoords: Pair<Int,Int>, endCoords: Pair<Int,Int>) {
        val capturePiece =
            viewModel.placements[endCoords.first][endCoords.second]
        val move = Move(startCoords, endCoords, capturePiece, EMPTY)
        val moveStatus = viewModel.rulesEngine.validateMove(move)
        when (moveStatus) {
            MoveStatus.ILLEGAL ->  makeIllegalMoveReaction(endCoords)//do nothing
            MoveStatus.LEGAL -> doMove(move)
            MoveStatus.CAPTURE -> doCaptureMove(move)
        }
        viewModel.rulesEngine.getNextPosition(move)

    }

    fun makePromotionDialog(coords: Pair<Int,Int>) {

    }

    fun makeIllegalMoveReaction(coords: Pair<Int,Int>) {

    }

    fun doMove(move: Move) {
        //TODO sound effect
        makeMove(move)
    }

    fun doCaptureMove(move: Move) {
        //TODO sound effect
        makeMove(move)
    }

    fun makeMove(move: Move) {

    }


}
