package com.secondslot.coursework.data.db.model

import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.model.Reaction
import java.util.*

class MessageEntity(
    val messageId: UUID = UUID.randomUUID(),
    val userId: Long = 0,
    val datetime: Long = 0L,
    val username: String = "",
    val userPhoto: String = "",
    val message: String = "",
    var reactions: ArrayList<Reaction> = arrayListOf()
)

fun MessageEntity.toDomainModel(): Message = Message(
    messageId = this.messageId,
    userId = this.userId,
    datetime = this.datetime,
    username = this.username,
    userPhoto = this.userPhoto,
    message = this.message,
    reactions = this.reactions
)
