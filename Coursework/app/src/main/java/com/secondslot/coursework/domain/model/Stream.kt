package com.secondslot.coursework.domain.model

data class Stream(
    val id: Int,
    val streamName: String,
    val description: String,
    val topics: List<Topic>
) {

    data class Topic(
        val topicName: String,
        val maxMessageId: Int,
        val streamId: Int,
        val isSubscribed: Boolean
    )
}
