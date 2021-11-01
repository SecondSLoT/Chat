package com.secondslot.coursework.domain.model

class User(
    val id: Int,
    val userPhoto: String = "",
    val username: String,
    val about: String = "",
    val email: String,
    val status: Int
) {

    companion object {
        const val STATUS_ONLINE = 0
        const val STATUS_OFFLINE = 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (username != other.username) return false
        if (about != other.about) return false
        if (email != other.email) return false
        if (status != other.status) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + username.hashCode()
        result = 31 * result + about.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }
}
