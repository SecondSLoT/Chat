package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.MessagesRepositoryImpl
import com.secondslot.coursework.data.api.model.SendResult
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Single

class SendMessageUseCase {

    private val repository: MessagesRepository = MessagesRepositoryImpl()

    fun execute(
        type: String = "stream",
        streamId: Int,
        topicName: String,
        messageText: String
    ): Single<SendResult> {
        return repository.sendMessage(type, streamId, topicName, messageText)
    }
}
