package com.leverett.rules.chess.parsing

import com.leverett.rules.chess.basic.BasicRulesEngine
import com.leverett.rules.chess.basic.piece.PieceRules
import com.leverett.rules.chess.representation.*

object PGNBuilder {

    private val rulesEngine = BasicRulesEngine

    private const val CHECK_CHAR = '+'
    private const val CHECKMATE_CHAR = '#'
    private const val KINGSIDE_CASTLE = "O-O"
    private const val QUEENSIDE_CASTLE = "O-O-O"
    private const val CAPTURE_CHAR = 'x'
    private const val PROMOTION_CHAR = '='

    fun makeMoveNotation(position: Position, move: Move): String {
        var legalitylessMove = ""
        if (move == WHITE_KINGSIDE_CASTLE || move == BLACK_KINGSIDE_CASTLE) {
            legalitylessMove = KINGSIDE_CASTLE
        } else if (move == WHITE_QUEENSIDE_CASTLE || move == BLACK_QUEENSIDE_CASTLE) {
                legalitylessMove = QUEENSIDE_CASTLE
        } else {
            val startLoc = move.startLoc
            val piece = position.pieceAt(startLoc)
            val pieceType = piece.type
            if (pieceType != Piece.PieceType.PAWN) {
                legalitylessMove += pieceType.pieceTypeChar
            }

            val endLoc = move.endLoc
            val pieceRules = getPieceRules(pieceType, endLoc) as PieceRules
            if (pieceType != Piece.PieceType.PAWN) {
                legalitylessMove += disambiguatePieceToken(position, startLoc, pieceRules)
            }
            if (move.capture != Piece.EMPTY) {
                // not done in the disambiguation as it always appears in notation regardless of ambiguity
                if (pieceType == Piece.PieceType.PAWN) {
                    legalitylessMove += fileToNotation(startLoc.first)
                }
                legalitylessMove += CAPTURE_CHAR
            }
            legalitylessMove += locationToNotation(endLoc)
            if (move.promotion != null) {
                legalitylessMove += (PROMOTION_CHAR.toString() + move.promotion.type.pieceTypeChar)
            }
        }

        return annotateLegality(legalitylessMove, rulesEngine.getNextPosition(position, move))
    }

    private fun disambiguatePieceToken(position: Position, startLoc: Pair<Int,Int>, pieceRules: PieceRules): String {
        val candidatePieceLocs = pieceRules!!.canMoveToCoordFrom(position.placements, position.activeColor)
        if (candidatePieceLocs.size == 1) {
            return ""
        }
        val candidatesFilteredByFile = candidatePieceLocs.filter { it.first == startLoc.first }
        if (candidatesFilteredByFile.size == 1) {
            return fileToNotation(startLoc.first).toString()
        }
        val candidatesFilteredByRank = candidatePieceLocs.filter { it.second == startLoc.second }
        if (candidatesFilteredByRank.size == 1) {
            return (startLoc.second + 1).toString()
        }
        return locationToNotation(startLoc)
    }


    private fun annotateLegality(moveToken: String, position: Position): String {
        val positionStatus = rulesEngine.positionStatus(position)
        return when {
            positionStatus.inCheck -> moveToken + CHECK_CHAR
            positionStatus.inCheckmate -> moveToken + CHECKMATE_CHAR
            else -> moveToken
        }
    }

}