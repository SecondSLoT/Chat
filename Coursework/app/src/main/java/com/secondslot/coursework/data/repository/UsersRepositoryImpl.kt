package com.secondslot.coursework.data.repository

import android.util.Log
import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.UserRemoteToUserMapper
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.data.db.model.entity.UserEntity
import com.secondslot.coursework.data.db.model.entity.UserEntityToUserMapper
import com.secondslot.coursework.data.db.model.entity.UserToUserEntityMapper
import com.secondslot.coursework.data.db.model.entity.toDomainModel
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@Reusable
class UsersRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val networkManager: NetworkManager
) : UsersRepository {

    private var myId = -1

    override fun getUsers(): Observable<List<User>> {

        // Data from DB
        val usersDbObservable = database.userDao
            .getAllUsers().map { userEntitiesList ->
                Log.d(TAG, "usersObservableDb size = ${userEntitiesList.size}")
                UserEntityToUserMapper.map(userEntitiesList)
            }.toObservable()

        // Data from network
        val usersRemoteObservable = networkManager.getAllUsers()
            .map { usersRemoteList ->
                UserRemoteToUserMapper.map(usersRemoteList)
            }
            // Save data from network to DB
            .flatMap { users ->
                database.userDao.updateUsers(UserToUserEntityMapper.map(users))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnComplete { Log.d(TAG, "updateUsers complete") }
                    .doOnError { Log.d(TAG, "updateUsers complete") }
                    .andThen(Observable.just(users))
            }
            .doOnError { Log.e(TAG, "usersRemoteObservable error") }

        // Return data from DB first, then from network
        return Observable.concat(
            usersDbObservable,
            usersRemoteObservable
        )
    }

    override fun getProfileInfo(userId: Int): Observable<List<User>> {

        // Data from DB
        val userDbObservable = database.userDao.getUser(userId)
            .map { listOf(it.toDomainModel()) }
            .toObservable()

        // Data from network
        val userRemoteObservable = networkManager.getUser(userId)
            .map { UserRemoteToUserMapper.map(it) }

        return Observable.concat(
            userDbObservable,
            userRemoteObservable
        )
    }

    override fun getOwnProfile(): Observable<List<User>> {

        // Data from DB
        val userDbObservable = if (myId != -1) {
            database.userDao.getUser(myId)
                .map { listOf(it.toDomainModel()) }
                .toObservable()
        } else {
            Observable.just(emptyList())
        }

        // Data from network
        val userRemoteObservable = networkManager.getOwnUser()
            .map {
                val users = UserRemoteToUserMapper.map(it)
                Log.d(TAG, "Insert User into DB")
                database.userDao.insertUser(UserEntity.fromDomainModel(users[0]))
                myId = users[0].userId
                users
            }

        return Observable
            .concat(
                userDbObservable,
                userRemoteObservable
            )
            .filter { it.isNotEmpty() }
    }

    companion object {
        private const val TAG = "UsersRepoImpl"
    }
}
