package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.data.api.model.SendResult
import com.secondslot.coursework.data.local.model.ReactionLocal

interface ReactionsRepository {

    suspend fun addReaction(messageId: Int, emojiName: String): SendResult

    suspend fun removeReaction(messageId: Int, emojiName: String): SendResult

    fun getReactions(): List<ReactionLocal>
}
