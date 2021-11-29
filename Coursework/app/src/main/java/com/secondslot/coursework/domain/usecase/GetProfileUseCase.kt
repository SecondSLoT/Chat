package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val usersRepository: UsersRepository
) {

    fun execute(userId: Int): Flow<List<User>> {
        return usersRepository.getProfileInfo(userId)
    }
}
