package com.leverett.rules.chess.parsing

import com.leverett.rules.chess.parsing.CoordinateParser.notationToCoordinate
import com.leverett.rules.chess.representation.Castling
import com.leverett.rules.chess.representation.PieceEnum
import com.leverett.rules.chess.representation.Position
import com.leverett.rules.chess.representation.Position.Companion.NEW_PLACEMENTS
import com.leverett.rules.chess.representation.Position.Companion.NO_ENPASSANT_TARGET

object FENParser {

    private const val FEN_TOKEN_LENGTH = 6

    private const val WHITE_CHAR = 'w'

    private const val DELIMITER = ' '
    private const val PLACEMENT_INDEX = 0
    private const val ACTIVE_COLOR_INDEX = 1
    private const val CASTLING_INDEX = 2
    private const val ENPASSANT_TARGET_INDEX = 3
    private const val TURN_INDEX = 5

    private const val WHITE_KING_CASTLE_CHAR = 'K'
    private const val WHITE_QUEEN_CASTLE_CHAR = 'Q'
    private const val BLACK_KING_CASTLE_CHAR = 'k'
    private const val BLACK_QUEEN_CASTLE_CHAR = 'q'

    const val STARTING_FEN: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

    fun positionFromFen(fen: String): Position {
        val tokens:List<String> = fen.split(DELIMITER)
        if (tokens.size != FEN_TOKEN_LENGTH) {
            //TODO throw error
        }
        val placements = placementsFromFen(tokens[PLACEMENT_INDEX])
        val activeColor = (tokens[ACTIVE_COLOR_INDEX].equals(WHITE_CHAR))
        val castling = castlingFromFen(tokens[CASTLING_INDEX])
        val enPassantTarget =
            if (tokens[ENPASSANT_TARGET_INDEX].equals(NO_ENPASSANT_TARGET)) Pair(-1,-1)
            else notationToCoordinate(tokens[ENPASSANT_TARGET_INDEX])
        val turn = tokens[TURN_INDEX].toInt()
        return Position(placements, activeColor, castling, enPassantTarget, turn)
    }

    private fun placementsFromFen(fen: String): Array<Array<PieceEnum>> {
        // TODO this should probably use split on the '/' char
        if (false) {
            //TODO simple validation
        }
        val placements: Array<Array<PieceEnum>> = NEW_PLACEMENTS
        var i = 0
        var j = 0
        var fenIndex = 0
        while (i < Position.GRID_SIZE && j < Position.GRID_SIZE) {
            var currentChar: Char = fen[fenIndex]
            if (currentChar == DELIMITER) {
                break
            }
            if (currentChar.isDigit()) {
                i += currentChar.digitToInt()
            } else if (currentChar.equals('/')) {
                j += 1
                i = 0
            } else {
                placements[i][Position.GRID_SIZE - j - 1] = PieceEnum.getPiece(currentChar);
            }
        }
        return placements
    }

    private fun castlingFromFen(fen: String): Castling {
        return Castling(
            fen.contains(WHITE_KING_CASTLE_CHAR),
            fen.contains(WHITE_QUEEN_CASTLE_CHAR),
            fen.contains(BLACK_KING_CASTLE_CHAR),
            fen.contains(BLACK_QUEEN_CASTLE_CHAR))
    }
}