package com.secondslot.coursework.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class StreamsResponse(
    @Json(name = "result") val result: String = "",
    @Json(name = "subscriptions") val streams: List<StreamRemote>
)
