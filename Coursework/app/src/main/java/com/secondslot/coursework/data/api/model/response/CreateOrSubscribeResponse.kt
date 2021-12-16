package com.secondslot.coursework.data.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CreateOrSubscribeResponse(
    @field:Json(name = "result") val result: String = ""
)
