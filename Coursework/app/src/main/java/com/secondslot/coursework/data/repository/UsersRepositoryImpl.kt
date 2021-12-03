package com.secondslot.coursework.data.repository

import android.util.Log
import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.toDomainModel
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.data.db.model.entity.UserEntity
import com.secondslot.coursework.data.db.model.entity.UserToUserEntityMapper
import com.secondslot.coursework.data.db.model.entity.toDomainModel
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val networkManager: NetworkManager
) : UsersRepository {

    private var myId = -1

    override fun getUsers(): Flow<List<User>> = flow {

        Log.d(TAG, "getUsers thread = ${Thread.currentThread().name}")

        // Data from DB
        val usersFromDb = database.userDao.getAllUsers().map { it.toDomainModel() }
        Log.d(TAG, "usersFromDb size = ${usersFromDb.size}")
        emit(usersFromDb)

        // Data from network
        val usersFromNetwork = networkManager.getAllUsers().map { it.toDomainModel() }
        emit(usersFromNetwork)

        // Save data from network to DB
        database.userDao.deleteAllUsers()
        database.userDao.insertUsers(UserToUserEntityMapper.map(usersFromNetwork))
    }
        .flowOn(Dispatchers.IO)

    override fun getProfileInfo(userId: Int): Flow<List<User>> = flow {

        // Data from DB
        try {
            val userFromDb = listOf(database.userDao.getUser(userId).toDomainModel())
            emit(userFromDb)
        } catch (e: Exception) {
            Log.d(TAG, "Can't find user in DB")
        }

        // Data from network
        val userFromNetwork = networkManager.getUser(userId).map { it.toDomainModel() }

        emit(userFromNetwork)
    }
        .flowOn(Dispatchers.IO)

    override fun getOwnProfile(): Flow<List<User>> = flow {

        // Data from DB
        val userFromDb = if (myId != -1) {
            listOf(database.userDao.getUser(myId).toDomainModel())
        } else {
            emptyList()
        }

        if (userFromDb.isNotEmpty()) emit(userFromDb)

        // Data from network
        val userFromNetwork = networkManager.getOwnUser().map { it.toDomainModel() }

        myId = userFromNetwork[0].userId

        // Save data from network to DB
        Log.d(TAG, "Insert User into DB")
        database.userDao.insertUser(UserEntity.fromDomainModel(userFromNetwork[0]))

        emit(userFromNetwork)
    }
        .flowOn(Dispatchers.IO)

    companion object {
        private const val TAG = "UsersRepoImpl"
    }
}
