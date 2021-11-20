package com.secondslot.coursework.data.api.model.response

import com.secondslot.coursework.data.api.model.SendResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SendResponse(
    @field:Json(name = "result") val result: String
)

fun SendResponse.toSendResult(): SendResult {
    return SendResult(result = this.result)
}
