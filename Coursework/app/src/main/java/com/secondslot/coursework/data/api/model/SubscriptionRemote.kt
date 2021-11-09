package com.secondslot.coursework.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SubscriptionRemote(
    @field:Json(name = "stream_id") val id: Int,
    @field:Json(name = "name") val streamName: String,
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "rendered_description") val renderedDescription: String?,
    @field:Json(name = "invite_only") val inviteOnly: Boolean?,
    @field:Json(name = "stream_post_policy") val streamPostPolicy: Int?,
    @field:Json(name = "history_public_to_subscribers") val historyPublicToSubscribers: Boolean?,
    @field:Json(name = "first_message_id") val firstMessageId: Int?,
    @field:Json(name = "message_retention_days") val messageRetentionDays: Int?,
    @field:Json(name = "date_created") val dateCreated: Int?,
    @field:Json(name = "color") val color: String?,
    @field:Json(name = "is_muted") val isMuted: Boolean?,
    @field:Json(name = "pin_to_top") val pinToTop: Boolean?,
    @field:Json(name = "audible_notifications") val audibleNotifications: Boolean?,
    @field:Json(name = "email_notifications") val emailNotifications: Boolean?,
    @field:Json(name = "push_notifications") val pushNotifications: Boolean?,
    @field:Json(name = "wildcard_mentions_notify") val wildcardMentionsNotify: Boolean?,
    @field:Json(name = "role") val role: Int,
    @field:Json(name = "stream_weekly_traffic") val streamWeeklyTraffic: Int?,
    @field:Json(name = "email_address") val streamEmailAddress: String?
)
