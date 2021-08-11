package com.leverett.chessrepertoirepractice.logging

import android.util.Log
import com.leverett.rules.chess.parsing.CoordinateParser
import com.leverett.rules.chess.representation.Castling
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.Position

class FENParserClass() {

    init {
        Log.e("FENParserClass", "HEEEERE")
    }
    private val _fenTokenLength = 6

    private val _whiteChar = 'w'

    private val _delimiter = ' '
    private val _placementIndex = 0
    private val _activeColorIndex = 1
    private val _castlingIndex = 2
    private val _enPassantTargetIndex = 3
    private val _turnIndex = 5

    private val _whiteKingCastleChar = 'K'
    private val _whiteQueenCastleChar = 'Q'
    private val _blackKingCastleChar = 'k'
    private val _blackQueenCastleChar = 'q'

    private val _startingFen: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

    fun startingPosition(): Position {
        Log.e("FENParserClass","HEREEEE1")
        return positionFromFen(_startingFen)
    }

    fun positionFromFen(fen: String): Position {
        Log.e("FENParserClass","HEREEEE2")
        val tokens:List<String> = fen.split(_delimiter)
        if (tokens.size != _fenTokenLength) {
            //TODO throw error
        }
        val placements = placementsFromFen(tokens[_placementIndex])
        val activeColor = (tokens[_activeColorIndex].equals(_whiteChar))
        val castling = castlingFromFen(tokens[_castlingIndex])
        val enPassantTarget =
            if (tokens[_enPassantTargetIndex].equals(Position.NO_ENPASSANT_TARGET)) Pair(-1,-1)
            else CoordinateParser.notationToCoordinate(tokens[_enPassantTargetIndex])
        val turn = tokens[_turnIndex].toInt()
        Log.e("FENParserClass","HEREEEE3")
        return Position(placements, activeColor, castling, enPassantTarget, turn)
    }

    private fun placementsFromFen(fen: String): Array<Array<PieceEnum>> {
        Log.e("FENParserClass","HEREEEE3")
        // TODO this should probably use split on the '/' char
        if (false) {
            //TODO simple validation
        }
        Log.e("FENParserClass","HEREEEE4")
        val placements = Position.NEW_PLACEMENTS
        Log.e("FENParserClass","HEREEEE6")
        var i = 0
        var j = 0
        var fenIndex = 0
        while (i < Position.GRID_SIZE && j < Position.GRID_SIZE) {
            Log.e("FENParserClass","HEREEEE: $i, $j")
            var currentChar: Char = fen[fenIndex]
            if (currentChar == _delimiter) {
                Log.e("FENParserClass","HEREEEE41")
                break
            }
            if (currentChar.isDigit()) {
                Log.e("FENParserClass","HEREEEE42")
                i += currentChar.digitToInt()
            } else if (currentChar.equals('/')) {

                Log.e("FENParserClass","HEREEEE43")
                j += 1
                i = 0
            } else {
                Log.e("FENParserClass","HEREEEE44")
                placements[i][Position.GRID_SIZE - j - 1] = PieceEnum.getPiece(currentChar);
            }
        }
        Log.e("FENParserClass","HEREEEE5")
        return placements
    }

    private fun castlingFromFen(fen: String): Castling {
        return Castling(
            fen.contains(_whiteKingCastleChar),
            fen.contains(_whiteQueenCastleChar),
            fen.contains(_blackKingCastleChar),
            fen.contains(_blackQueenCastleChar))
    }
}