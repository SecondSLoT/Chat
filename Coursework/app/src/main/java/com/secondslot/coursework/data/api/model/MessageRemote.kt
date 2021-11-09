package com.secondslot.coursework.data.api.model

import com.secondslot.coursework.core.mapper.BaseMapper
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.model.Reaction
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MessageRemote(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "sender_id") val senderId: Int,
    @field:Json(name = "sender_full_name") val senderFullName: String?,
    @field:Json(name = "avatar_url") val avatarUrl: String?,
    @field:Json(name = "content") val content: String,
    @field:Json(name = "subject") val topic: String?,
    @field:Json(name = "recipient_id") val recipientId: String?,
    @field:Json(name = "timestamp") val timestamp: Int,
    @field:Json(name = "client") val client: String?,
    @field:Json(name = "is_me_message") val isMeMessage: Boolean,
    @field:Json(name = "reactions") val reactions: List<ReactionRemote>,
    @field:Json(name = "flags") val flags: List<String>,
    @field:Json(name = "sender_email") val senderEmail: String?,
    @field:Json(name = "sender_realm_str") val senderRealmStr: String?,
    @field:Json(name = "type") val type: String?,
    @field:Json(name = "stream_id") val streamId: Int?,
    @field:Json(name = "content_type") val contentType: String?,
)

object MessageRemoteToDomainMapper : BaseMapper<List<MessageRemote>, List<Message>> {

    override fun map(type: List<MessageRemote>?): List<Message> {
        return type?.map {
            Message(
                id = it.id,
                senderId = it.senderId,
                senderFullName = it.senderFullName,
                avatarUrl = it.avatarUrl,
                content = it.content,
                topic = it.topic,
                timestamp = it.timestamp,
                isMeMessage = it.isMeMessage,
                reactions = it.reactions.map { reactionRemote ->
                    Reaction(
                        emojiName = reactionRemote.emojiName,
                        emojiCode = String(
                            Character.toChars(reactionRemote.emojiCode.toInt(16))
                        ),
                        reactionType = reactionRemote.reactionType,
                        user = reactionRemote.user,
                        userId = reactionRemote.userId
                    )
                }
            )
        } ?: emptyList()
    }
}
