package com.secondslot.coursework.data.api.model

import com.secondslot.coursework.core.mapper.BaseMapper
import com.secondslot.coursework.domain.model.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserRemote(
    @field:Json(name = "user_id") val userId: Int,
    @field:Json(name = "full_name") val fullName: String,
    @field:Json(name = "avatar_url") val avatarUrl: String?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "avatar_version") val avatarVersion: Int?,
    @field:Json(name = "is_admin") val isAdmin: Boolean?,
    @field:Json(name = "is_owner") val isOwner: Boolean?,
    @field:Json(name = "is_guest") val isGuest: Boolean?,
    @field:Json(name = "is_billing_admin") val isBillingAdmin: Boolean?,
    @field:Json(name = "role") val role: Int?,
    @field:Json(name = "is_bot") val isBot: Boolean?,
    @field:Json(name = "timezone") val timezone: String?,
    @field:Json(name = "is_active") val isActive: Boolean?,
    @field:Json(name = "date_joined") val dateJoined: String?,
    @field:Json(name = "max_message_id") val maxMessageId: Int?,
)

object UserRemoteToUserMapper : BaseMapper<List<UserRemote>, List<User>> {

    override fun map(type: List<UserRemote>?): List<User> {
        return type?.map {
            it.toDomainModel()
        } ?: emptyList()
    }
}

fun UserRemote.toDomainModel(): User = User(
    userId = this.userId,
    fullName = this.fullName,
    avatarUrl = this.avatarUrl,
    email = this.email
)
