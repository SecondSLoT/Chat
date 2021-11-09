package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.UserRemoteToUserMapper
import com.secondslot.coursework.data.api.model.toDomainModel
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Observable
import io.reactivex.Single

object UsersRepositoryImpl : UsersRepository {

    private val networkManager = NetworkManager()

    override fun getUsers(): Observable<List<User>> {
        return networkManager.getAllUsers().map { userRemote ->
            UserRemoteToUserMapper.map(userRemote)
        }
    }

    override fun getProfileInfo(userId: Int): Single<User> {
        return networkManager.getUser(userId).map { it.toDomainModel() }
    }

    override fun getOwnProfile(): Single<User> {
        return networkManager.getOwnUser().map { it.toDomainModel() }
    }
}
