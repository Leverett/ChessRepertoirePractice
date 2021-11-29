package com.leverett.chessrepertoirepractice.utils

import android.content.Context
import android.util.DisplayMetrics

const val SQUARE_DIMENSIONS = "1:1"

fun popupWidthDp(context: Context, ratio: Float): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    return (displayMetrics.widthPixels * ratio / displayMetrics.density).toInt()
}