package com.secondslot.coursework.data.api.model.response

import com.secondslot.coursework.data.api.model.ServerResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ServerResponse(
    @field:Json(name = "result") val result: String
)

fun ServerResponse.toServerResult(): ServerResult {
    return ServerResult(result = this.result)
}
