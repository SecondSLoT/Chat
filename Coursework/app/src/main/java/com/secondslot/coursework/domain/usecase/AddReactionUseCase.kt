package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.api.model.SendResult
import com.secondslot.coursework.domain.repository.ReactionsRepository
import io.reactivex.Single
import javax.inject.Inject

class AddReactionUseCase @Inject constructor(
    private val reactionsRepository: ReactionsRepository
) {

    fun execute(
        messageId: Int,
        emojiName: String
    ): Single<SendResult> {
        return reactionsRepository.addReaction(messageId, emojiName)
    }
}
