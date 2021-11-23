package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Observable

class GetOwnProfileUseCase(
    private val usersRepository: UsersRepository
) {


    fun execute(): Observable<List<User>> {
        return usersRepository.getOwnProfile()
    }
}
