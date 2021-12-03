package com.secondslot.coursework.features.channels.vm

import androidx.lifecycle.ViewModel
import com.secondslot.coursework.core.Event
import com.secondslot.coursework.domain.usecase.GetAllStreamsUseCase
import com.secondslot.coursework.domain.usecase.GetSubscribedStreamsUseCase
import com.secondslot.coursework.features.channels.core.StreamsListType
import com.secondslot.coursework.features.channels.model.ExpandableStreamModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class StreamsListViewModel(
    private val getSubscribedStreamsUseCase: GetSubscribedStreamsUseCase,
    private val getAllStreamsUseCase: GetAllStreamsUseCase
) : ViewModel() {

    private val _searchQueryStateFlow = MutableStateFlow("")
    val retryFlow = MutableStateFlow(Event(false))

    fun loadStreams(viewType: String): Flow<List<ExpandableStreamModel>> =
        when (viewType) {
            StreamsListType.SUBSCRIBED -> {
                getSubscribedStreamsUseCase.execute()
                    .map { ExpandableStreamModel.fromStream(it) }
            }
            else -> {
                getAllStreamsUseCase.execute()
                    .map { ExpandableStreamModel.fromStream(it) }
            }
        }

    fun searchStreams(searchQuery: String) {
        _searchQueryStateFlow.value = searchQuery
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun observeSearchChanges(viewType: String): Flow<List<ExpandableStreamModel>> {
        return _searchQueryStateFlow.asStateFlow()
            .debounce(500)
            .flatMapLatest { searchQuery ->
                when (viewType) {
                    StreamsListType.SUBSCRIBED -> {
                        getSubscribedStreamsUseCase.execute(searchQuery)
                            .map { ExpandableStreamModel.fromStream(it) }
                    }
                    else -> {
                        getAllStreamsUseCase.execute(searchQuery)
                            .map { ExpandableStreamModel.fromStream(it) }
                    }
                }
            }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun onRetryClicked(viewType: String) {
        retryFlow.value = Event(true)
    }

    companion object {
        private const val TAG = "StreamsListViewModel"
    }
}
