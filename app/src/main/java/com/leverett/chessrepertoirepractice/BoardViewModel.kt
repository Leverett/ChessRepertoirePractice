package com.leverett.chessrepertoirepractice

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import com.leverett.chessrepertoirepractice.logging.BasicRulesEngineLoggable
import com.leverett.chessrepertoirepractice.logging.FENParserClass
import com.leverett.rules.RulesEngine
import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.Position

class BoardViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    init {
        Log.e("BoardViewModel", "HEEEERE")
    }
    private val parser = FENParserClass()
    init {
        Log.e("BoardViewModel", "HEEEERE11")
    }
    private var position : Position = parser.startingPosition()
        set(value) {
            Log.e("BoardViewModel", "HEREEE2")
            position = value
            rulesEngine.position = position
        }
    init {
        Log.e("BoardViewModel", "HEEEERE3")
    }
    var rulesEngine: RulesEngine
    init {
        Log.e("BoardViewModel", "HEREEE4")
        rulesEngine = BasicRulesEngineLoggable(position)
    }
    val placements: Array<Array<PieceEnum>>
        get() {
            return position.placements
        }
    var activeSquareCoords: Pair<Int, Int>? = null

    // TODO Make this a setting option
    val lightColor = Color.WHITE
    val darkColor = Color.BLACK
    val activeColor = Color.BLUE

}