package com.secondslot.coursework.features.profile.vm

import androidx.lifecycle.ViewModel
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.usecase.GetOwnProfileUseCase
import com.secondslot.coursework.domain.usecase.GetProfileUseCase
import kotlinx.coroutines.flow.Flow

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val getOwnProfileUseCase: GetOwnProfileUseCase
) : ViewModel() {

    fun loadProfile(userId: Int): Flow<List<User>> =
        if (userId != -1) {
            getProfileUseCase.execute(userId)
        } else {
            getOwnProfileUseCase.execute()
        }
}
