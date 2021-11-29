package com.secondslot.coursework.features.profile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.secondslot.coursework.domain.usecase.GetOwnProfileUseCase
import com.secondslot.coursework.domain.usecase.GetProfileUseCase
import javax.inject.Inject

class ProfileViewModelFactory @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getOwnProfileUseCase: GetOwnProfileUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(getProfileUseCase, getOwnProfileUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
