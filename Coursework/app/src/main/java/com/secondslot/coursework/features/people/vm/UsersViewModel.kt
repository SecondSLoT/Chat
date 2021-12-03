package com.secondslot.coursework.features.people.vm

import androidx.lifecycle.ViewModel
import com.secondslot.coursework.core.Event
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.usecase.GetAllUsersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext

class UsersViewModel(
    private val getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {

    private val _searchQueryStateFlow = MutableStateFlow("")

    val openUserFlow = MutableStateFlow(Event(-1))
    val retryFlow = MutableStateFlow(Event(false))

    suspend fun loadUsers(): Flow<List<User>> {
        return getAllUsersUseCase.execute()
    }

    fun searchUsers(searchQuery: String) {
        _searchQueryStateFlow.value = searchQuery
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    suspend fun observeSearchChanges(): Flow<List<User>> = withContext(Dispatchers.IO) {
        _searchQueryStateFlow.asStateFlow()
            .debounce(500)
            .flatMapLatest { searchQuery -> getAllUsersUseCase.execute(searchQuery) }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun onRetryClicked() {
        retryFlow.value = Event(true)
    }

    fun onUserClicked(userId: Int) {
        openUserFlow.value = Event(userId)
    }

    companion object {
        private const val TAG = "UsersViewModel"
    }
}
