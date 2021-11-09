package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.domain.model.User
import io.reactivex.Observable
import io.reactivex.Single

interface UsersRepository {

    fun getUsers(): Observable<List<User>>

    fun getProfileInfo(userId: Int): Single<User>

    fun getOwnProfile(): Single<User>
}
