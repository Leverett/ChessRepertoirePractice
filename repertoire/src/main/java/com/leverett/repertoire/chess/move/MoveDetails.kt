package com.leverett.repertoire.chess.move

class MoveDetails(var description: String? = null) {

    val tags: MutableList<Tag> = mutableListOf()
    val best: Boolean
        get() = tags.contains(Tag.BEST)
    val theory: Boolean
        get() = tags.contains(Tag.THEORY)
    val gambit: Boolean
        get() = tags.contains(Tag.GAMBIT)
    val preferred: Boolean
        get() = tags.contains(Tag.PREFERRED)
    val mistake: Boolean
        get() = tags.contains(Tag.MISTAKE)

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