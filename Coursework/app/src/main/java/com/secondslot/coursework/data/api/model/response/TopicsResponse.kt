package com.secondslot.coursework.data.api.model.response

import com.secondslot.coursework.data.api.model.TopicRemote
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TopicsResponse(
    @field:Json(name = "result") val result: String = "",
    @field:Json(name = "topics") val topics: List<TopicRemote>
)
