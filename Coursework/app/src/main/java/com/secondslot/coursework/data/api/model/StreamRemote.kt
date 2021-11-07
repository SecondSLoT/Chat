package com.secondslot.coursework.data.api.model

import com.secondslot.coursework.core.mapper.BaseMapper
import com.secondslot.coursework.domain.model.Stream
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StreamRemote(
    @Json(name = "stream_id") val id: Int,
    @Json(name = "name") val streamName: String,
    @Json(name = "description") val description: String?,
    @Json(name = "rendered_description") val renderedDescription: String?,
    @Json(name = "invite_only") val inviteOnly: Boolean?,
    @Json(name = "stream_post_policy") val streamPostPolicy: Int?,
    @Json(name = "history_public_to_subscribers") val historyPublicToSubscribers: Boolean?,
    @Json(name = "first_message_id") val firstMessageId: Int?,
    @Json(name = "message_retention_days") val messageRetentionDays: Int?,
    @Json(name = "date_created") val dateCreated: Int?,
    @Json(name = "color") val color: String?,
    @Json(name = "is_muted") val isMuted: Boolean?,
    @Json(name = "pin_to_top") val pinToTop: Boolean?,
    @Json(name = "audible_notifications") val audibleNotifications: Boolean?,
    @Json(name = "email_notifications") val emailNotifications: Boolean?,
    @Json(name = "push_notifications") val pushNotifications: Boolean?,
    @Json(name = "wildcard_mentions_notify") val wildcardMentionsNotify: Boolean?,
    @Json(name = "role") val role: Int,
    @Json(name = "stream_weekly_traffic") val streamWeeklyTraffic: Int?,
    @Json(name = "email_address") val streamEmailAddress: String?,
)

//object StreamToDomainMapper : BaseMapper<List<StreamRemote>, List<Stream>> {
//
//    override fun map(type: List<StreamRemote>?): List<Stream> {
//        return type?.map {
//            Stream(
//                id = it.id,
//                streamName = it.streamName,
//                description = it.description ?: "",
//                color = it.color ?: "",
//                role = it.role,
//                topics = it.topics.map { topicRemote ->
//                    Stream.Topic(
//                        id = topicRemote.id,
//                        topicName = topicRemote.topicName
//                    )
//                }
//            )
//        } ?: emptyList()
//    }
//}
