package com.secondslot.coursework.data.api.model.response

import com.secondslot.coursework.data.api.model.StreamRemote
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SubscriptionsResponse(
    @field:Json(name = "result") val result: String = "",
    @field:Json(name = "subscriptions") val streams: List<StreamRemote>
)
