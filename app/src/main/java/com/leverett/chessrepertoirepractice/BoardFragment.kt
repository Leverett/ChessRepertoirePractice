package com.leverett.chessrepertoirepractice

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import androidx.gridlayout.widget.GridLayout.LayoutParams
import androidx.lifecycle.ViewModelProvider
import com.leverett.chessrepertoirepractice.repertoire.representation.PieceChars.EMPTY
import com.leverett.chessrepertoirepractice.repertoire.representation.Position.Companion.GRID_SIZE
import com.leverett.chessrepertoirepractice.ui.views.SquareView


class BoardFragment : Fragment() {

    companion object {
        fun newInstance() = BoardFragment()
    }

    private lateinit var viewModel: BoardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.board_fragment, container, false)
        val boardLayout = view.findViewById<GridLayout>(R.id.board_layout)

        val squareSize: Int = boardLayout.width / GRID_SIZE
        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {
                Log.i("BoardFrag", String.format("i: $i, j: $j"))
//                val color: Int = if ((i + j) % 2 == 0) Color.WHITE else Color.BLACK
//                val squareImage = makeSquareView(squareSize, color)
//                val layoutParams = GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j))
//                boardLayout.addView(squareImage, layoutParams)
//                val squareView = SquareView(context, null, color, EMPTY, i, j)
                val drawable = if ((i + j) % 2 == 0) R.drawable.light_square else R.drawable.dark_square
                val squareView = ImageView(context).also { it.setImageResource(drawable) }
                view.visibility = View.VISIBLE
                val param = LayoutParams()
                param.height = LayoutParams.WRAP_CONTENT
                param.width = LayoutParams.WRAP_CONTENT
                param.columnSpec = GridLayout.spec(j, 1)
                param.rowSpec = GridLayout.spec(i, 1)
                squareView.layoutParams = param
                boardLayout.addView(squareView, i * GRID_SIZE + j)
                Log.i("BoardFrag", String.format("imageview size " + squareView.height))
            }
        }

        val childCount = boardLayout.childCount
        val width = boardLayout.measuredWidth
        Log.i("BoardFrag", String.format("childCount  $childCount, width $width"))
        boardLayout.refreshDrawableState()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BoardViewModel::class.java)


        // TODO: Use the ViewModel
    }

}