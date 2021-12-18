package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.domain.model.Message
import io.reactivex.Observable
import io.reactivex.Single

interface MessagesRepository {

    fun getMessages(
        anchor: String,
        numBefore: String,
        numAfter: String,
        narrow: Map<String, Any>
    ): Observable<List<Message>>

    fun sendMessage(
        type: String,
        streamId: Int,
        topicName: String,
        messageText: String
    ): Single<ServerResult>

    fun deleteMessage(
        messageId: Int
    ): Single<ServerResult>

    fun editMessage(
        messageId: Int,
        newMessageText: String
    ): Single<ServerResult>

    fun moveMessage(
        messageId: Int,
        newTopic: String
    ): Single<ServerResult>
}
