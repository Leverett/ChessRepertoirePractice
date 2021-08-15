package com.leverett.repertoire.chess.lines

class Repertoire(name: String, val books: List<Book>, description: String? = null) : LineTreeSet(name, books, description) {
}