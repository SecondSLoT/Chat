package com.secondslot.coursework.data.repository

import android.util.Log
import com.secondslot.coursework.App
import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.UserRemoteToUserMapper
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.data.db.model.entity.UserEntity
import com.secondslot.coursework.data.db.model.entity.UserEntityToUserMapper
import com.secondslot.coursework.data.db.model.entity.UserToUserEntityMapper
import com.secondslot.coursework.data.db.model.entity.toDomainModel
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

object UsersRepositoryImpl : UsersRepository {

    private const val TAG = "UsersRepoImpl"

    private val database: AppDatabase = App.getAppDatabase()
    private val networkManager = NetworkManager()

    private var myId = -1

    override fun getUsers(): Observable<List<User>> {

        // Data from DB
        val usersDbObservable = database.userDao
            .getAllUsers().map { userEntitiesList ->
                Log.d(TAG, "usersObservableDb size = ${userEntitiesList.size}")
                UserEntityToUserMapper.map(userEntitiesList)
            }.toObservable()

        // Data from network
        val usersRemoteObservable = networkManager.getAllUsers().map { usersRemoteList ->
            UserRemoteToUserMapper.map(usersRemoteList)
        }

        // Save data from network to DB
        val disposable = usersRemoteObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onNext = { users ->
                    val deleteCompletable = database.userDao.deleteAllUsers()
                    val insertCompletable = database.userDao.insertUsers(
                        UserToUserEntityMapper.map(users)
                    )

                    val disposable = deleteCompletable.concatWith(insertCompletable)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribeBy(
                            onComplete = {
                                Log.d(TAG, "updateUsers complete")
                            },
                            onError = { Log.d(TAG, "updateUsers error") }
                        )
                },
                onError = { Log.e(TAG, "usersRemoteObservable error") }
            )

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

        val userRemoteObservable = networkManager.getOwnUser()
            .map { UserRemoteToUserMapper.map(it) }

        val disposable = userRemoteObservable
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    Log.d(TAG, "Insert User into DB")
                    database.userDao.insertUser(UserEntity.fromDomainModel(it[0]))
                    myId = it[0].userId
                }
            )

        return Observable
            .concat(
                userDbObservable,
                userRemoteObservable
            )
            .filter { it.isNotEmpty() }
    }
}
