package com.secondslot.coursework.features.chat.model

import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.model.Reaction
import java.util.*

class MessageItem(
    val messageId: UUID = UUID.randomUUID(),
    val userId: Long = 0,
    val datetime: Long = 0L,
    val username: String = "",
    val userPhoto: String = "",
    val message: String = "",
    var reactions: ArrayList<Reaction> = arrayListOf()
) : ChatItem {

    companion object {
        fun fromDomainModel(messages: List<Message>): List<MessageItem> =
            messages.map {
                MessageItem(
                    messageId = it.messageId,
                    userId = it.userId,
                    datetime = it.datetime,
                    username = it.username,
                    userPhoto = it.userPhoto,
                    message = it.message,
                    reactions = it.reactions
                )
            }

    }
}
