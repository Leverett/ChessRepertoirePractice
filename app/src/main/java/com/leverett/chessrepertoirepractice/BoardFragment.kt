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
import com.leverett.rules.chess.representation.GRID_SIZE
import com.leverett.rules.chess.representation.Move
import com.leverett.rules.chess.representation.MoveStatus
import com.leverett.rules.chess.representation.PieceEnum.EMPTY


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
        val view: View = inflater.inflate(R.layout.board_fragment, container, false)
        val boardLayout = view.findViewById<ConstraintLayout>(R.id.board_layout)
        val context = requireContext()
        viewModel = BoardViewModel(this)

        squares = Array(GRID_SIZE) {x -> Array(GRID_SIZE) {y -> SquareLayout(context, viewModel, x, y).also{boardLayout.addView(it)}} }

        for (x in 0 until GRID_SIZE) {
            for (y in 0 until GRID_SIZE) {
                val square = squares[x][y]
                val layoutParams = ConstraintLayout.LayoutParams(0, 0)
                layoutParams.dimensionRatio = "1:1"
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
//            setOnDragListener(boardLayout)
        }
        return view
    }

    fun updateSquaresToPosition() {
        for (x in 0 until GRID_SIZE) {
            for (y in 0 until GRID_SIZE) {
                squares[x][y].updateSquare()
            }
        }
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
//
//
//        // TODO: Use the ViewModel
//    }
    private fun setOnDragListener(board: ConstraintLayout) {
        //TODO dragging
//        board.setOnDragListener{ view, event ->
//            if (view is SquareLayout) {
//                val startCoords = view.coords
//            }
//            false
//        }
    }


}
