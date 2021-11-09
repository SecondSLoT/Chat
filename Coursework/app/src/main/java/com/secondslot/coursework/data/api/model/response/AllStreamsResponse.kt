package com.secondslot.coursework.data.api.model.response

import com.secondslot.coursework.data.api.model.StreamRemote
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AllStreamsResponse(
    @field:Json(name = "result") val result: String = "",
    @field:Json(name = "streams") val streams: List<StreamRemote>
)
