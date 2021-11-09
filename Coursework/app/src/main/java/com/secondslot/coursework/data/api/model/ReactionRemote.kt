package com.secondslot.coursework.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReactionRemote(
    @field:Json(name = "emoji_name") val emojiName: String,
    @field:Json(name = "emoji_code") val emojiCode: String,
    @field:Json(name = "reaction_type") val reactionType: String,
    @field:Json(name = "user") val user: UserShortRemote,
    @field:Json(name = "user_id") val userId: Int
)
