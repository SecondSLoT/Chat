package com.secondslot.coursework.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserShortRemote(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "full_name") val fullName: String,
    @field:Json(name = "email") val email: String
)
