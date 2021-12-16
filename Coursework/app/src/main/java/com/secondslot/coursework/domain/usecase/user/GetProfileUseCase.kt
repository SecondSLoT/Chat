package com.secondslot.coursework.domain.usecase.user

import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val usersRepository: UsersRepository
) {

    fun execute(userId: Int): Observable<List<User>> {
        return usersRepository.getProfileInfo(userId)
    }
}
