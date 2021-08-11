package com.leverett.chessrepertoirepractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.Position
import com.leverett.rules.chess.representation.Position.Companion.GRID_SIZE

class SandboxActivity : AppCompatActivity() {

    private lateinit var newplacements: Array<Array<PieceEnum>>
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("SandboxActivity","HEREEEE")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sandbox)
        Log.e("SandboxActivity","HEREEEE2")
        newplacements = Position.NEW_PLACEMENTS
        Log.e("SandboxActivity","HEREEEE3")
    }
}