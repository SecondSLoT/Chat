package com.secondslot.coursework.features.channels.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.secondslot.coursework.domain.usecase.GetAllStreamsUseCase
import com.secondslot.coursework.domain.usecase.GetSubscribedStreamsUseCase
import javax.inject.Inject

class StreamsListViewModelFactory @Inject constructor(
    private val getSubscribedStreamsUseCase: GetSubscribedStreamsUseCase,
    private val getAllStreamsUseCase: GetAllStreamsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreamsListViewModel::class.java)) {
            return StreamsListViewModel(getSubscribedStreamsUseCase, getAllStreamsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
