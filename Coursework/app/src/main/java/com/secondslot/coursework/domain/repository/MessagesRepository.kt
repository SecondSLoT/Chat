package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.domain.SendResult
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
    ): Single<SendResult>
}
