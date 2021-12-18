package com.secondslot.coursework.domain.usecase.message

import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Single
import javax.inject.Inject

class MoveMessageUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
) {

    fun execute(
        messageId: Int,
        newTopic: String
    ): Single<ServerResult> {
        return messagesRepository.moveMessage(messageId, newTopic)
    }
}
