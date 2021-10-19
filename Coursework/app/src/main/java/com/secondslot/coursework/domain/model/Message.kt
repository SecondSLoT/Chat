package com.secondslot.coursework.domain.model

import java.util.*

class Message(
    val messageId: UUID = UUID.randomUUID(),
    val userId: Long = 0,
    val datetime: Long = 0L,
    val username: String = "",
    val userPhoto: String = "",
    val message: String = "",
    var reactions: ArrayList<Reaction> = arrayListOf()
) : ChatItem
