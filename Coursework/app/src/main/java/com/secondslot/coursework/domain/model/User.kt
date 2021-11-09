package com.secondslot.coursework.domain.model

data class User(
    val userId: Int,
    val fullName: String,
    val avatarUrl: String?,
    val email: String?,
    val isMe: Boolean = false
)
