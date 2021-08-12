package com.leverett.rules.chess.parsing

import com.leverett.rules.chess.representation.*
import com.leverett.rules.log
import java.util.logging.Level
import java.util.logging.Logger

private const val FEN_TOKEN_LENGTH = 6

private const val WHITE_CHAR = "w"

private const val ROW_DELIMITER = '/'

private const val DELIMITER = " "
private const val PLACEMENT_INDEX = 0
private const val ACTIVE_COLOR_INDEX = 1
private const val CASTLING_INDEX = 2
private const val ENPASSANT_TARGET_INDEX = 3
private const val TURN_INDEX = 5

private const val NO_ENPASSANT_TARGET = '-'

const val STARTING_FEN: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

fun positionFromFen(fen: String): Position {
    val tokens:List<String> = fen.split(DELIMITER)
    if (tokens.size != FEN_TOKEN_LENGTH) {
        //TODO throw error
    }
    val placements = placementsFromFen(tokens[PLACEMENT_INDEX])
    val activeColor = (tokens[ACTIVE_COLOR_INDEX].equals(WHITE_CHAR))
    log("FENPARSING", tokens[ACTIVE_COLOR_INDEX])
    log("FENPARSING", activeColor.toString())
    val castling = castlingFromFen(tokens[CASTLING_INDEX])
    val enPassantTarget =
        if (tokens[ENPASSANT_TARGET_INDEX] == NO_ENPASSANT_TARGET.toString()) NO_ENPASSANT_TARGET_COORDINATE
        else notationToCoordinate(tokens[ENPASSANT_TARGET_INDEX])
    val turn = tokens[TURN_INDEX].toInt()
    return Position(placements, activeColor, castling, enPassantTarget, turn)
}

private fun placementsFromFen(fenToken: String): Array<Array<PieceEnum>> {
    // TODO this should probably use split on the '/' char
    if (false) {
        //TODO simple validation
    }
    val placements: Array<Array<PieceEnum>> = newPlacements()
    var i = 0
    var j = 0
    var fenIndex = 0
    while (fenIndex < fenToken.length) {
        var currentChar: Char = fenToken[fenIndex]
        if (currentChar.isDigit()) {
            i += currentChar.digitToInt()
        } else if (currentChar == ROW_DELIMITER) {
            j++
            i = 0
        } else {
            placements[i][GRID_SIZE - j - 1] = PieceEnum.getPiece(currentChar);
            i++
        }
        fenIndex++
    }
    return placements
}

private fun castlingFromFen(fenToken: String): Castling {
    return Castling(
        fenToken.contains(WHITE_KING_CHAR),
        fenToken.contains(WHITE_QUEEN_CHAR),
        fenToken.contains(BLACK_KING_CHAR),
        fenToken.contains(BLACK_QUEEN_CHAR))
}