package com.leverett.repertoire.chess

import java.lang.IllegalArgumentException

class MoveDetails(var description: String? = null) {

    private val tags: MutableList<Tag> = mutableListOf()
    val isTheory: Boolean
        get() = tags.contains(Tag.THEORY)
    val isMistake: Boolean
        get() = tags.contains(Tag.MISTAKE)
    val isPreferred: Boolean
        get() = tags.contains(Tag.PREFERRED)
    val isGambitLine: Boolean
        get() = tags.contains(Tag.GAMBIT)
    val isBestMove: Boolean
        get() = tags.contains(Tag.BEST)

    fun addTag(tag: Tag?) {
        if (tag != null) tags.add(tag)
    }

    fun copy(): MoveDetails {
        val copy = MoveDetails(description)
        for (tag in tags) {
            copy.addTag(tag)
        }
        return copy
    }

    enum class Tag {
        THEORY,
        MISTAKE,
        PREFERRED,
        GAMBIT,
        BEST
    }
}

// TODO This is probably unnecessary
//fun getTag(tagToken: String): MoveDetails.Tag? {
//    try {
//        return MoveDetails.Tag.valueOf(tagToken)
//    } catch (exception: IllegalArgumentException) {
//        if (tagToken.first().isDigit()) {
//            return null
//        }
//        //if it's not a digit, it's a bad tag that I added, not lichess, and should throw the error
//        throw exception
//    }
//}