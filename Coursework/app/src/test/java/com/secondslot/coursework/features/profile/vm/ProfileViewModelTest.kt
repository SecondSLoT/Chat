package com.secondslot.coursework.features.profile.vm

import com.secondslot.coursework.domain.usecase.GetOwnProfileUseCase
import com.secondslot.coursework.domain.usecase.GetProfileUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

    lateinit var profileViewModel: ProfileViewModel

    @Mock
    lateinit var getProfileUseCase: GetProfileUseCase

    @Mock
    lateinit var getOwnProfileUseCase: GetOwnProfileUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        profileViewModel = ProfileViewModel(getProfileUseCase, getOwnProfileUseCase)
    }

    @Test
    fun loadUserProfileByIdTest() {
        profileViewModel.loadProfile(5)
        verify(getProfileUseCase).execute(5)
        verify(getProfileUseCase, never()).execute(4)
    }

    @Test
    fun loadOwnProfileTest() {
        profileViewModel.loadProfile(-1)
        verify(getOwnProfileUseCase).execute()
    }
}
