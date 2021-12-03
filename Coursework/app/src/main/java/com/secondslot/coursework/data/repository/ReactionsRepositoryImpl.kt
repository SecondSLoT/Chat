package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.SendResult
import com.secondslot.coursework.data.api.model.response.toSendResult
import com.secondslot.coursework.data.local.ReactionStorage
import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.domain.repository.ReactionsRepository
import javax.inject.Inject

class ReactionsRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager
) : ReactionsRepository {

    override suspend fun addReaction(messageId: Int, emojiName: String): SendResult {
        return networkManager.addReaction(messageId, emojiName).toSendResult()
    }

    override suspend fun removeReaction(messageId: Int, emojiName: String): SendResult {
        return networkManager.removeReaction(messageId, emojiName).toSendResult()
    }

    override fun getReactions(): List<ReactionLocal> {
        return ReactionStorage.reactions
    }
}
