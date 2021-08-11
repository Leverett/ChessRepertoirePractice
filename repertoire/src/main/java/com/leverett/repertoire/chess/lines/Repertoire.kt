package com.leverett.repertoire.chess.lines

class Repertoire(private val books: List<Book>, description: String) : LineTreeSet(books, description) {
}