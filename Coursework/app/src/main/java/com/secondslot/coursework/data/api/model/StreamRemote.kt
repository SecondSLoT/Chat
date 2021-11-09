package com.secondslot.coursework.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class StreamRemote(
    @field:Json(name = "stream_id") val id: Int,
    @field:Json(name = "name") val streamName: String,
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "invite_only") val inviteOnly: Boolean?
)
