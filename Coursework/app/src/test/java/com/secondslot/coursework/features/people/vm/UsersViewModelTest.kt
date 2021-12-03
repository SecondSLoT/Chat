package com.secondslot.coursework.features.people.vm

import com.secondslot.coursework.domain.usecase.GetAllUsersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class UsersViewModelTest {

    lateinit var usersViewModel: UsersViewModel

    lateinit var getAllUsersUseCase: GetAllUsersUseCase

    @Before
    fun setUp() {
        getAllUsersUseCase = Mockito.mock(GetAllUsersUseCase::class.java)
        usersViewModel = UsersViewModel(getAllUsersUseCase)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadUsersTest() = runBlockingTest {
        usersViewModel.loadUsers()
        verify(getAllUsersUseCase).execute()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Test
    fun observeSearchChanges() {
        val result = usersViewModel.observeSearchChanges()
        assertThat(result, instanceOf(Flow::class.java))
    }

}
