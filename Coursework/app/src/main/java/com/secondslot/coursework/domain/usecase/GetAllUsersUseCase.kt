package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val usersRepository: UsersRepository
) {

    suspend fun execute(searchQuery: String = ""): Flow<List<User>> =
        withContext(Dispatchers.IO) {
            if (searchQuery.isEmpty()) {
                usersRepository.getUsers()
            } else {
                usersRepository.getUsers().map { users ->
                    users.filter {
                        it.fullName.contains(searchQuery, ignoreCase = true)
                    }
                }
            }
        }
}
