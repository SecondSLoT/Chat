package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.data.api.model.SendResult
import io.reactivex.Single

interface ReactionsRepository {

    fun addReaction(messageId: Int, emojiName: String): Single<SendResult>

    fun removeReaction(messageId: Int, emojiName: String): Single<SendResult>
}
