package com.secondslot.coursework.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopicRemote(
    @Json(name = "max_id") val id: Int,
    @Json(name = "name") val topicName: String,
)
