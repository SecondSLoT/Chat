package com.secondslot.coursework.domain.usecase.user

import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val usersRepository: UsersRepository
) {

    fun execute(searchQuery: String = ""): Observable<List<User>> {
        return if (searchQuery.isEmpty()) {
            usersRepository.getUsers()
        } else {
            usersRepository.getUsers()
                .map { users ->
                    users.filter {
                        it.fullName.contains(searchQuery, ignoreCase = true)
                    }
                }
        }
    }
}
