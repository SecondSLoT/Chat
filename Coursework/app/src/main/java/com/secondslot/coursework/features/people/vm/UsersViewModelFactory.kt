package com.secondslot.coursework.features.people.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.secondslot.coursework.domain.usecase.GetAllUsersUseCase
import javax.inject.Inject

class UsersViewModelFactory @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            return UsersViewModel(getAllUsersUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
