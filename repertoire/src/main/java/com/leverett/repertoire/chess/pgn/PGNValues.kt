package com.leverett.repertoire.chess.pgn

const val CHAPTER_DELIMITER = "\n\n\n"
const val HEADER_DELIMITER = "\n\n"
const val BOOK_CHAPTER_NAME_DELIMITER = ": "
const val NAME_PREFIX = "Event "
const val CHAPTER_DESCRIPTION_PREFIX = "ChapterDescription "
const val BOOK_DESCRIPTION_PREFIX = "BookDescription "
const val METADATA_TOKEN_START = "["
const val METADATA_TOKEN_END = "]"
const val METADATA_VALUE_TAG = "\""
const val MOVE_DELIMITER = ' '
const val COMMENT_START = '{'
const val COMMENT_END = '}'
const val BRANCH_START = '('
const val BRANCH_END = ')'
const val GRAPHIC_START = '['
const val GRAPHIC_END = ']'
const val TAG_CHAR = '$'

// only appears in castling, and the number indicates the castling side regardless of O's vs 0's
const val CASTLE_CHAR = '-'
const val CHECK_CHAR = '+'
const val CHECKMATE_CHAR = '#'
const val PROMOTION_CHAR = '='
const val CAPTURE_CHAR = 'x'
const val CHAPTER_END = '*'
const val KINGSIDE_CASTLE = "O-O"
const val QUEENSIDE_CASTLE = "O-O-O"