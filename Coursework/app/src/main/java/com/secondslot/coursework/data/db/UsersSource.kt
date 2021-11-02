package com.secondslot.coursework.data.db

import androidx.annotation.WorkerThread
import com.secondslot.coursework.data.db.model.UserEntity
import com.secondslot.coursework.domain.model.User

object UsersSource {

    private val userList = listOf(
        UserEntity(
            id = 1,
            userPhoto = "test_image",
            username = "Mr. Baggins",
            about = "Available",
            email = "baggins@gmail.com",
            status = User.STATUS_ONLINE
        ),
        UserEntity(
            id = 2,
            userPhoto = "test_image",
            username = "Darrell Steward",
            about = "Some text here",
            email = "arrell@company.com",
            status = User.STATUS_OFFLINE
        )
    )

    @WorkerThread
    fun getUsers(): List<UserEntity> {
        return userList
    }

    @WorkerThread
    fun getUserProfile(userId: Int): UserEntity =
        userList.find { userId == it.id } ?: throw Throwable("Can't find user")

    @WorkerThread
    fun getMyProfile(): UserEntity = UserEntity(
        id = 0,
        userPhoto = "test_image",
        username = "My name",
        about = "In a meeting",
        email = "myemail@company.com",
        status = User.STATUS_ONLINE
    )
}
