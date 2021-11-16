package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.domain.model.User
import io.reactivex.Observable

interface UsersRepository {

    fun getUsers(): Observable<List<User>>

    fun getProfileInfo(userId: Int): Observable<List<User>>

    fun getOwnProfile(): Observable<List<User>>
}
