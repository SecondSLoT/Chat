package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.api.model.SendResult
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Single
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
) {

    fun execute(
        type: String = "stream",
        streamId: Int,
        topicName: String,
        messageText: String
    ): Single<SendResult> {
        return messagesRepository.sendMessage(type, streamId, topicName, messageText)
    }
}
