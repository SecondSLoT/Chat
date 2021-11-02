package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.db.UsersSource
import com.secondslot.coursework.data.db.model.toDomainModel
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import com.secondslot.coursework.util.Temporary
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class UsersRepositoryImpl : UsersRepository {

    override fun getUsers(): Observable<List<User>> {
        return Observable.create<List<User>> { emitter ->
            if (Temporary.imitateError()) {
                emitter.onError(Throwable("Error getting users list"))
            } else {
                emitter.onNext(UsersSource.getUsers().map { it.toDomainModel() })
            }
        }
            .delay(1000L, TimeUnit.MILLISECONDS)
    }

    override fun getProfileInfo(userId: Int): Single<User> {
        return if (userId == 0) {
            Single.fromCallable { UsersSource.getMyProfile().toDomainModel() }
                .delay(1000L, TimeUnit.MILLISECONDS)
        } else {
            Single.fromCallable { UsersSource.getUserProfile(userId).toDomainModel() }
                .delay(1000L, TimeUnit.MILLISECONDS)
        }
    }
}
