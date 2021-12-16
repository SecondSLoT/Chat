package com.secondslot.coursework.domain.usecase.message

import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Single
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
) {

    fun execute(
        messageId: Int
    ): Single<ServerResult> {
        return messagesRepository.deleteMessage(messageId)
    }
}
