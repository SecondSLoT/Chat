package com.secondslot.coursework.domain.model

data class Stream(
    val id: Int,
    val streamName: String,
    val description: String,
    val color: String,
    val role: Int,
    val topics: List<Topic>
) {

    data class Topic(
        val id: Int,
        val topicName: String,
    )
}

