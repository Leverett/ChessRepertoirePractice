package com.leverett.chessrepertoirepractice.utils

import android.graphics.Color

enum class BoardStyle(val lightSquareColor: Int,
                      val darkSquareColor: Int,
                      val activeSquareColor: Int,
                      val promotionBackground: Int) {

    Standard(Color.WHITE, Color.DKGRAY, Color.BLUE, Color.WHITE),
    Red(Color.WHITE, Color.RED, Color.BLUE, Color.WHITE);
}