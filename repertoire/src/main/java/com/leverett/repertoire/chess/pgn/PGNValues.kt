package com.leverett.repertoire.chess.pgn

const val CHAPTER_DELIMITER = "\n\n\n"
const val HEADER_DELIMITER = "\n\n"
const val BOOK_CHAPTER_NAME_DELIMITER = ": "
const val NAME_PREFIX = "Event "
const val CHAPTER_DESCRIPTION_PREFIX = "ChapterDescription "
const val BOOK_DESCRIPTION_PREFIX = "BookDescription "
const val FEN_PREFIX = "FEN "
const val METADATA_TOKEN_START = "["
const val METADATA_TOKEN_END = "]"
const val METADATA_VALUE_TAG = "\""
const val TOKEN_DELIMITER = ' '
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

const val GOOD_ANNOTATION = "!"
const val BRILLIANT_ANNOTATION = "!!"
const val MISTAKE_ANNOTATION = "?"
const val WHITE_INITIATIVE_ANNOTATION = "\$36"
const val BLACK_INITIATIVE_ANNOTATION = "\$37"
const val WHITE_ATTACK_ANNOTATION = "\$40"
const val BLACK_ATTACK_ANNOTATION = "\$41"