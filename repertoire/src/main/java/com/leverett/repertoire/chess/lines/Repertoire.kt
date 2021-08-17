package com.leverett.repertoire.chess.lines

class Repertoire(name: String, val books: Collection<Book>, description: String? = null) : LineTreeSet(name, books, description) {
}