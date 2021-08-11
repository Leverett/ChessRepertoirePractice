package com.leverett.chessrepertoirepractice

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

    }
    private fun makeSquareView(size: Int, color: Int): ImageView {
        val squareDrawable = ShapeDrawable(RectShape())
        squareDrawable.intrinsicHeight = size
        squareDrawable.intrinsicWidth = size
        val squareImage = ImageView(applicationContext)
        squareImage.setImageDrawable(squareDrawable)
        DrawableCompat.setTint(DrawableCompat.wrap(squareDrawable), color)
        return squareImage
    }
}