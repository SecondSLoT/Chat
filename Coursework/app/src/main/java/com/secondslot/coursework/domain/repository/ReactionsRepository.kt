package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.data.local.model.ReactionLocal
import io.reactivex.Single

interface ReactionsRepository {

    fun addReaction(messageId: Int, emojiName: String): Single<ServerResult>

    fun removeReaction(messageId: Int, emojiName: String): Single<ServerResult>

    fun getReactions(): List<ReactionLocal>
}
