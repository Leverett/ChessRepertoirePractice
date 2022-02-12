package com.leverett.repertoire.chess.move

class MoveDetails(var description: String? = null) {

    val tags: MutableSet<Tag> = mutableSetOf()
    val best: Boolean
        get() = tags.contains(Tag.BEST)
    val theory: Boolean
        get() = best || tags.contains(Tag.THEORY)
    val gambit: Boolean
        get() = tags.contains(Tag.GAMBIT)
    val preferred: Boolean
        get() = tags.contains(Tag.PREFERRED)
    val mistake: Boolean
        get() = tags.contains(Tag.MISTAKE)
    val first: Boolean
        get() = tags.contains(Tag.FIRST)

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
        BEST,
        FIRST
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is MoveDetails) {
            return false
        }
        if (other.description != description) {
            return false
        }
        if (!other.tags.containsAll(tags) || !tags.containsAll(other.tags)) {
            return false
        }
        return true
    }
}