package com.secondslot.coursework.domain.model

class Reaction(
    val code: String,
    var count: Int = 1,
    var isSelected: Boolean = false
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Reaction

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
