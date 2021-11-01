package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.UsersRepositoryImpl
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Single

class GetProfileUseCase {

    private val repository: UsersRepository = UsersRepositoryImpl()

    fun execute(userId: Int): Single<User> {
        return repository.getProfileInfo(userId)
    }
}
