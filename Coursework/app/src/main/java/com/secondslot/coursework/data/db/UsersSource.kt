package com.secondslot.coursework.data.db

import androidx.annotation.WorkerThread
import com.secondslot.coursework.domain.model.User

object UsersSource {

    private val userList = listOf(
        User(
            id = 1,
            userPhoto = "test_image.png",
            username = "Mr. Baggins",
            about = "Available",
            email = "baggins@gmail.com",
            status = User.STATUS_ONLINE
        ),
        User(
            id = 2,
            userPhoto = "test_image.png",
            username = "Darrell Steward",
            about = "Some text here",
            email = "arrell@company.com",
            status = User.STATUS_OFFLINE
        )
    )

    @WorkerThread
    fun getUsers(): List<User> {
        return userList
    }

    @WorkerThread
    fun getUserProfile(userId: Int): User =
        userList.find { userId == it.id } ?: throw Throwable("Can't find user")

    @WorkerThread
    fun getMyProfile(): User = User(
        id = 0,
        username = "My name",
        about = "In a meeting",
        email = "myemail@company.com",
        status = User.STATUS_ONLINE
    )
}
