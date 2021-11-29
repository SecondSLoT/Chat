package com.secondslot.coursework.features.people.vm

import androidx.lifecycle.ViewModel
import com.secondslot.coursework.core.Event
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.domain.usecase.GetAllUsersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class UsersViewModel(
    private val getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {

    private val _searchQueryStateFlow = MutableStateFlow("")

    val openUserFlow = MutableStateFlow(Event(-1))

    fun loadUsers(): Flow<List<User>> = getAllUsersUseCase.execute()

    fun searchUsers(searchQuery: String) {
        _searchQueryStateFlow.value = searchQuery
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun observeSearchChanges(): Flow<List<User>> {
        return _searchQueryStateFlow.asStateFlow()
            .debounce(500)
            .flatMapLatest { searchQuery -> getAllUsersUseCase.execute(searchQuery) }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun onRetryClicked() {
        loadUsers()
        observeSearchChanges()
    }

    fun onUserClicked(userId: Int) {
        openUserFlow.value = Event(userId)
    }

    companion object {
        private const val TAG = "UsersViewModel"
    }
}
