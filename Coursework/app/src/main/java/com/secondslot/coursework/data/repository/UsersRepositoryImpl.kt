package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.db.UsersSource
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class UsersRepositoryImpl : UsersRepository {

    override fun getUsers(): Observable<List<User>> {
        return Observable.create<List<User>> { emitter ->
            if (Random.nextBoolean()) emitter.onNext(UsersSource.getUsers())
            else emitter.onError(Throwable("Error getting users list"))
        }
            .delay(1000L, TimeUnit.MILLISECONDS)
    }

    override fun getProfileInfo(userId: Int): Single<User> {
        return if (userId == 0) {
            Single.fromCallable { UsersSource.getMyProfile() }
                .delay(1000L, TimeUnit.MILLISECONDS)
        } else {
            Single.fromCallable { UsersSource.getUserProfile(userId) }
                .delay(1000L, TimeUnit.MILLISECONDS)
        }
    }
}
