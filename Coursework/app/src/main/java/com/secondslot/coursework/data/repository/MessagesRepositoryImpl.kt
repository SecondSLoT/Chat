package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.MessageRemoteToDomainMapper
import com.secondslot.coursework.data.api.model.response.toSendResult
import com.secondslot.coursework.domain.SendResult
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Observable
import io.reactivex.Single

class MessagesRepositoryImpl : MessagesRepository {

    private val networkManager = NetworkManager()

    override fun getMessages(
        anchor: String,
        numBefore: String,
        numAfter: String,
        narrow: Map<String, Any>
    ): Observable<List<Message>> {
        return networkManager.getMessages(anchor, numBefore, numAfter, narrow)
            .map { messageRemoteList ->
                MessageRemoteToDomainMapper.map(messageRemoteList)
            }
    }

    override fun sendMessage(
        type: String,
        streamId: Int,
        topicName: String,
        messageText: String
    ): Single<SendResult> {
        return networkManager.sendMessage(type, streamId, topicName, messageText)
            .map { it.toSendResult() }
    }
}
