package com.leverett.chessrepertoirepractice.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.gridlayout.widget.GridLayout
import com.leverett.chessrepertoirepractice.repertoire.representation.Position.Companion.GRID_SIZE

class SquareView(context: Context?, attributeSet: AttributeSet?, val color: Int, val piece: Char, val i: Int, val j: Int): View(context, attributeSet) {

//    var x: Int = -1
//    var y: Int = -1
    private val paint: Paint = Paint()

    override fun onDraw(canvas: Canvas) {
        val gridLayout = parent as GridLayout
        val squareSize = (gridLayout.width / GRID_SIZE).toFloat()
        val index = gridLayout.indexOfChild(this)
        Log.i("SquareView", String.format("index $index, i $i , j $j, squareSize $squareSize"))
//        setValues(gridLayout.indexOfChild(this))
//        Log.i("GRIDLAYOUT INDEX", gridLayout.indexOfChild(this).toString())
        canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
    }


//    private fun setValues(index: Int) {
//        // TODO flip board
//        x = index % GRID_SIZE
//        y = GRID_SIZE - (index / GRID_SIZE) - 1
//        // TODO color settings
//        if (index % 2 == 0) paint.color = Color.WHITE else paint.color = Color.BLACK
//    }
}