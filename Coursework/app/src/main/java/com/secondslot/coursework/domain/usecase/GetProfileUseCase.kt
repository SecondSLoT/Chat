package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.UsersRepositoryImpl
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Observable

class GetProfileUseCase {

    private val repository: UsersRepository = UsersRepositoryImpl
    fun execute(userId: Int): Observable<List<User>> {
        return repository.getProfileInfo(userId)
    }
}
