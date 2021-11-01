package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.UsersRepositoryImpl
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.repository.UsersRepository
import io.reactivex.Observable

class GetUsersUseCase {

    private val usersRepository: UsersRepository = UsersRepositoryImpl()

    fun execute(searchQuery: String = ""): Observable<List<User>> {
        return if (searchQuery.isEmpty()) {
            usersRepository.getUsers()
        } else {
            usersRepository.getUsers()
                .map { users ->
                    users.filter {
                        it.username.contains(searchQuery, ignoreCase = true)
                    }
                }
        }
    }
}
