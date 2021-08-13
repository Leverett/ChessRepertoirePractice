package com.leverett.chessrepertoirepractice.utils

import android.graphics.Color

enum class BoardStyle(val lightSquareColor: Int,
                      val darkSquareColor: Int,
                      val activeSquareColor: Int,
                      val promotionBackground: Int) {

    STANDARD(Color.WHITE, Color.DKGRAY, Color.BLUE, Color.WHITE);
}