package com.leverett.rules.chess.parsing

import com.leverett.rules.chess.representation.*

private const val FEN_TOKEN_LENGTH = 6

private const val WHITE_CHAR = "w"
private const val BLACK_CHAR = "b"

private const val ROW_DELIMITER = '/'

private const val DELIMITER = " "
private const val PLACEMENT_INDEX = 0
private const val ACTIVE_COLOR_INDEX = 1
private const val CASTLING_INDEX = 2
private const val ENPASSANT_TARGET_INDEX = 3
private const val TURN_INDEX = 5

const val NO_CASTLING = '-'
private const val NO_ENPASSANT_TARGET = '-'

const val STARTING_FEN: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

fun positionFromFen(fen: String): Position {
    val tokens:List<String> = fen.split(DELIMITER)
    if (tokens.size != FEN_TOKEN_LENGTH) {
        //TODO throw error
    }
    val placements = placementsFromFen(tokens[PLACEMENT_INDEX])
    val activeColor = (tokens[ACTIVE_COLOR_INDEX] == WHITE_CHAR)
    val castling = castlingFromFen(tokens[CASTLING_INDEX])
    val enPassantTarget =
        if (tokens[ENPASSANT_TARGET_INDEX] == NO_ENPASSANT_TARGET.toString()) null
        else notationToLocation(tokens[ENPASSANT_TARGET_INDEX])
    val turn = tokens[TURN_INDEX].toInt()
    return Position(placements, activeColor, castling, enPassantTarget, turn)
}

private fun placementsFromFen(fenToken: String): Array<Array<Piece>> {
    // TODO this should probably use split on the '/' char
    if (false) {
        //TODO simple validation
    }
    val placements: Array<Array<Piece>> = newPlacements()
    var i = 0
    var j = 0
    var fenIndex = 0
    while (fenIndex < fenToken.length) {
        var currentChar: Char = fenToken[fenIndex]
        when {
            currentChar.isDigit() -> i+= currentChar.digitToInt()
            currentChar == ROW_DELIMITER -> {j++; i = 0}
            else -> { placements[i][GRID_SIZE - j - 1] = getPiece(currentChar); i++ }
        }
        fenIndex++
    }
    return placements
}

private fun castlingFromFen(fenToken: String): Castling {
    return Castling(fenToken.contains(WHITE_KING_CHAR),
                  fenToken.contains(WHITE_QUEEN_CHAR),
                  fenToken.contains(BLACK_KING_CHAR),
                  fenToken.contains(BLACK_QUEEN_CHAR))
}

fun fenFromPosition(position: Position): String {
    val statelessFen = statelessFenFromPosition(position)
    val halfmoveClockToken = "0" //TODO Halfmove clock
    val fullmoveClockToken = position.turn.toString()
    return arrayOf(statelessFen, halfmoveClockToken, fullmoveClockToken).joinToString(DELIMITER)
}

fun statelessFenFromPosition(position: Position): String {
    val placementsToken = fenPlacementsFromPosition(position)
    val activeColorToken = if (position.activeColor) WHITE_CHAR else BLACK_CHAR
    val castlingToken = position.castling.toString()

    val enPassantTarget = position.enPassantTarget
    val enPassantTargetToken =
        if (enPassantTarget != null)
            locationToNotation(enPassantTarget)
        else
            NO_ENPASSANT_TARGET

    return arrayOf(placementsToken, activeColorToken, castlingToken, enPassantTargetToken).joinToString(DELIMITER)
}

private fun fenPlacementsFromPosition(position: Position): String {
    val placements = position.placements
    var result = ""
    var emptyCount = 0
    for (i in 0 until GRID_SIZE) {
        for (j in 0 until GRID_SIZE) {
            val piece = placements[i][GRID_SIZE - j - 1]
            if (piece == Piece.EMPTY) {
                emptyCount++
            } else {
                if (emptyCount > 0) {
                    result += emptyCount
                    emptyCount = 0
                }
                result += piece.pieceChar
            }
        }
        if (emptyCount > 0) {
            result += emptyCount
            emptyCount = 0
        }
        result += ROW_DELIMITER
    }

    return result
}