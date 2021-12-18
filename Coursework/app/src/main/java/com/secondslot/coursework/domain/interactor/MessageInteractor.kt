package com.secondslot.coursework.domain.interactor

import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.usecase.message.DeleteMessageUseCase
import com.secondslot.coursework.domain.usecase.message.EditMessageUseCase
import com.secondslot.coursework.domain.usecase.message.GetMessagesUseCase
import com.secondslot.coursework.domain.usecase.message.MoveMessageUseCase
import com.secondslot.coursework.domain.usecase.message.SendMessageUseCase
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class MessageInteractor @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val editMessageUseCase: EditMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val moveMessageUseCase: MoveMessageUseCase
) {

    fun getMessages(
        anchor: String = "newest",
        numBefore: String = "0",
        numAfter: String = "0",
        narrow: Map<String, Any>
    ): Observable<List<Message>> {
        return getMessagesUseCase.execute(anchor, numBefore, numAfter, narrow)
    }

    fun sendMessage(
        type: String = "stream",
        streamId: Int,
        topicName: String,
        messageText: String
    ): Single<ServerResult> {
        return sendMessageUseCase.execute(type, streamId, topicName, messageText)
    }

    fun deleteMessage(
        messageId: Int
    ): Single<ServerResult> {
        return deleteMessageUseCase.execute(messageId)
    }

    fun editMessage(
        messageId: Int,
        newMessageText: String
    ): Single<ServerResult> {
        return editMessageUseCase.execute(messageId, newMessageText)
    }

    fun moveMessage(
        messageId: Int,
        newTopic: String
    ): Single<ServerResult> {
        return moveMessageUseCase.execute(messageId, newTopic)
    }
}
