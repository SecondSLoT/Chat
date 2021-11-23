package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Observable

class GetProfileUseCase(
    private val usersRepository: UsersRepository
) {

    fun execute(userId: Int): Observable<List<User>> {
        return usersRepository.getProfileInfo(userId)
    }
}
