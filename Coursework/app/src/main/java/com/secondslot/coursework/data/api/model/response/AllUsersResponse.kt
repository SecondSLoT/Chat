package com.secondslot.coursework.data.api.model.response

import com.secondslot.coursework.data.api.model.UserRemote
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AllUsersResponse(
    @field:Json(name = "result") val result: String,
    @field:Json(name = "members") val users: List<UserRemote>
)
