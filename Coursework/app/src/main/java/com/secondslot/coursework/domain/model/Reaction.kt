package com.secondslot.coursework.domain.model

import com.secondslot.coursework.data.api.model.UserShortRemote

class Reaction(
    val emojiName: String,
    val emojiCode: String,
    val reactionType: String,
    val user: UserShortRemote,
    val userId: Int,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Reaction

        if (emojiName != other.emojiName) return false
        if (emojiCode != other.emojiCode) return false
        if (reactionType != other.reactionType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = emojiName.hashCode()
        result = 31 * result + emojiCode.hashCode()
        result = 31 * result + reactionType.hashCode()
        return result
    }
}
