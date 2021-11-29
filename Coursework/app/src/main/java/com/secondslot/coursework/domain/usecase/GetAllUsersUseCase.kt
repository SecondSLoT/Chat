package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val usersRepository: UsersRepository
) {

    fun execute(searchQuery: String = ""): Flow<List<User>> {
        return if (searchQuery.isEmpty()) {
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
