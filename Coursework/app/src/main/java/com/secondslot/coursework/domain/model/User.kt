package com.secondslot.coursework.domain.model

data class User(
    val userId: Int,
    val fullName: String,
    val avatarUrl: String?,
    val email: String?,
    val dateJoined: String?
) {
    override fun toString(): String {
        return "$userId - $fullName - $dateJoined"
    }
}
