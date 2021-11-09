package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.response.toSendResult
import com.secondslot.coursework.domain.SendResult
import com.secondslot.coursework.domain.repository.ReactionsRepository
import io.reactivex.Single

class ReactionsRepositoryImpl : ReactionsRepository {

    private val networkManager = NetworkManager()

    override fun addReaction(messageId: Int, emojiName: String): Single<SendResult> {
        return networkManager.addReaction(messageId, emojiName).map { it.toSendResult() }
    }

    override fun removeReaction(messageId: Int, emojiName: String): Single<SendResult> {
        return networkManager.removeReaction(messageId, emojiName).map { it.toSendResult() }
    }
}
