package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {

    fun getUsers(): Flow<List<User>>

    fun getProfileInfo(userId: Int): Flow<List<User>>

    fun getOwnProfile(): Flow<List<User>>
}
