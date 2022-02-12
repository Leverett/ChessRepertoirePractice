package com.leverett.repertoire.chess.move

import com.leverett.repertoire.chess.lines.Chapter
import com.leverett.repertoire.chess.pgn.makeMoveNotation
import com.leverett.rules.chess.representation.MoveAction
import com.leverett.rules.chess.representation.Position

class LineMove(val moveDefinition: MoveDefinition,
               val moveDetails: MoveDetails,
               val previousLineMove: LineMove?,
               val chapterName: String,
               val bookName: String?,
               algMove: String? = null) {

    constructor(chapter: Chapter,
                moveDefinition: MoveDefinition,
                moveDetails: MoveDetails,
                previousLineMove: LineMove?,
                algMove: String? = null) :
            this
                (moveDefinition,
                moveDetails,
                previousLineMove,
                chapter.chapterName,
                chapter.book?.name,
                algMove
                )

    val previousPosition: Position
        get() = moveDefinition.previousPosition
    val nextPosition: Position
        get() = moveDefinition.nextPosition
    val moveAction: MoveAction
        get() = moveDefinition.moveAction


    val best: Boolean
        get() = moveDetails.best
    val theory: Boolean
        get() = moveDetails.theory
    val gambit: Boolean
        get() = moveDetails.gambit
    val preferred: Boolean
        get() = moveDetails.preferred
    val mistake: Boolean
        get() = moveDetails.mistake
    val fullName: String
        get() = if (bookName != null) {"$bookName : $chapterName"} else chapterName

    // Better to make this on construction and store in than do it on demand, saves work overall I think
    val algMove: String = algMove ?: makeAlgMove()

    private fun makeAlgMove(): String {
        return makeMoveNotation(previousPosition, moveAction)
    }

    override fun toString(): String {
        return algMove
    }
}