package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.UsersRepositoryImpl
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Observable

class GetOwnProfileUseCase {

    private val repository: UsersRepository = UsersRepositoryImpl

    fun execute(): Observable<List<User>> {
        return repository.getOwnProfile()
    }
}
