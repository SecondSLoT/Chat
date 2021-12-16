package com.secondslot.coursework.domain.model

data class Message(
    val id: Int,
    val senderId: Int,
    val senderFullName: String?,
    val avatarUrl: String?,
    var content: String,
    val topicName: String?,
    val timestamp: Int,
    val isMeMessage: Boolean,
    var reactions: List<Reaction>
)
