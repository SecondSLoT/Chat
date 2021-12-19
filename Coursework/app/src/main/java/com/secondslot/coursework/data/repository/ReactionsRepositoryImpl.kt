package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.data.api.model.response.toServerResult
import com.secondslot.coursework.data.local.ReactionStorage
import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.domain.repository.ReactionsRepository
import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class ReactionsRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager
) : ReactionsRepository {

    override fun addReaction(messageId: Int, emojiName: String): Single<ServerResult> {
        return networkManager.addReaction(messageId, emojiName).map { it.toServerResult() }
    }

    override fun removeReaction(messageId: Int, emojiName: String): Single<ServerResult> {
        return networkManager.removeReaction(messageId, emojiName).map { it.toServerResult() }
    }

    override fun getReactions(): List<ReactionLocal> {
        return ReactionStorage.reactions
    }
}
