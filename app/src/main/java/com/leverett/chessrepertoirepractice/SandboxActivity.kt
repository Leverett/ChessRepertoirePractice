package com.leverett.chessrepertoirepractice

import android.os.Bundle
import com.leverett.rules.chess.representation.Move

class SandboxActivity : ChessActivity() {

    override val boardId = R.id.sandbox_board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sandbox)
    }

    override fun handleMove(move: Move) {
        // Nothing, this is a sandbox
    }
}