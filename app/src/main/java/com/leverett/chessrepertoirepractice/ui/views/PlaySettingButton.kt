package com.leverett.chessrepertoirepractice.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.leverett.chessrepertoirepractice.R

/**
 * TODO: document your custom view class.
 */
class PlaySettingButton(context: Context, attributeSet: AttributeSet) : AppCompatTextView(context, attributeSet) {

    var active: Boolean = false
    private val baseColor = Color.LTGRAY
    private val activeColor: Int
    init {
        val theme = context.theme.obtainStyledAttributes(attributeSet, R.styleable.PlaySettingButton, 0, 0)
        activeColor = theme.getColor(R.styleable.PlaySettingButton_activeColor, Color.LTGRAY)
    }

    fun updateColor() {
        if (active) setBackgroundColor(activeColor) else setBackgroundColor(baseColor)
    }

}