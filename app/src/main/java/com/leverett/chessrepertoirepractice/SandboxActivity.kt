package com.leverett.chessrepertoirepractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.leverett.rules.RulesEngine
import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.representation.GRID_SIZE
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.newPlacements

class SandboxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sandbox)
    }
}