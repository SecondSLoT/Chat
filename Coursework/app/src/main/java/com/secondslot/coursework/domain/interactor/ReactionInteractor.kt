package com.secondslot.coursework.domain.interactor

import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.domain.usecase.reaction.AddReactionUseCase
import com.secondslot.coursework.domain.usecase.reaction.GetReactionsUseCase
import com.secondslot.coursework.domain.usecase.reaction.RemoveReactionUseCase
import io.reactivex.Single
import javax.inject.Inject

class ReactionInteractor @Inject constructor(
    private val addReactionUseCase: AddReactionUseCase,
    private val getReactionsUseCase: GetReactionsUseCase,
    private val removeReactionUseCase: RemoveReactionUseCase
) {

    fun addReaction(
        messageId: Int,
        emojiName: String
    ): Single<ServerResult> {
        return addReactionUseCase.execute(messageId, emojiName)
    }

    fun getReactions(): List<ReactionLocal> {
        return getReactionsUseCase.execute()
    }

    fun removeReaction(
        messageId: Int,
        emojiName: String
    ): Single<ServerResult> {
        return removeReactionUseCase.execute(messageId, emojiName)
    }
}
