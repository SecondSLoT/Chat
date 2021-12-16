package com.secondslot.coursework.domain.usecase.reaction

import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.domain.repository.ReactionsRepository
import io.reactivex.Single
import javax.inject.Inject

class AddReactionUseCase @Inject constructor(
    private val reactionsRepository: ReactionsRepository
) {

    fun execute(
        messageId: Int,
        emojiName: String
    ): Single<ServerResult> {
        return reactionsRepository.addReaction(messageId, emojiName)
    }
}
