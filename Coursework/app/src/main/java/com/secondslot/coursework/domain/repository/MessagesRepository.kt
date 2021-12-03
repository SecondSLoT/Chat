package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.data.api.model.SendResult
import com.secondslot.coursework.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {

    fun getMessages(
        anchor: String,
        numBefore: String,
        numAfter: String,
        narrow: Map<String, Any>
    ): Flow<List<Message>>

    suspend fun sendMessage(
        type: String,
        streamId: Int,
        topicName: String,
        messageText: String
    ): SendResult
}
