package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.response.toSendResult
import com.secondslot.coursework.data.api.model.SendResult
import com.secondslot.coursework.data.local.ReactionStorage
import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.domain.repository.ReactionsRepository
import io.reactivex.Single

class ReactionsRepositoryImpl(
    private val networkManager: NetworkManager
) : ReactionsRepository {

    override fun addReaction(messageId: Int, emojiName: String): Single<SendResult> {
        return networkManager.addReaction(messageId, emojiName).map { it.toSendResult() }
    }

    override fun removeReaction(messageId: Int, emojiName: String): Single<SendResult> {
        return networkManager.removeReaction(messageId, emojiName).map { it.toSendResult() }
    }

    override fun getReactions(): List<ReactionLocal> {
        return ReactionStorage.reactions
    }
}
