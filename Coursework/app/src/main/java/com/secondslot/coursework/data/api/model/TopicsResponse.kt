package com.secondslot.coursework.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TopicsResponse(
    @Json(name = "result") val result: String = "",
    @Json(name = "topics") val topics: List<TopicRemote>
)
