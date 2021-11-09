package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.ReactionsRepositoryImpl
import com.secondslot.coursework.domain.SendResult
import com.secondslot.coursework.domain.repository.ReactionsRepository
import io.reactivex.Single

class RemoveReactionUseCase {

    private val repository: ReactionsRepository = ReactionsRepositoryImpl()

    fun execute(
        messageId: Int,
        emojiName: String
    ): Single<SendResult> {
        return repository.removeReaction(messageId, emojiName)
    }
}
