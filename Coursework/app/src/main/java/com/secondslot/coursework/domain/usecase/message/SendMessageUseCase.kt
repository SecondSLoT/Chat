package com.secondslot.coursework.domain.usecase.message

import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Single
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
) {

    fun execute(
        type: String,
        streamId: Int,
        topicName: String,
        messageText: String
    ): Single<ServerResult> {
        return messagesRepository.sendMessage(type, streamId, topicName, messageText)
    }
}
