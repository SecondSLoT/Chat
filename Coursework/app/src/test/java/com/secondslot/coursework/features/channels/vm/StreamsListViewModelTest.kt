package com.secondslot.coursework.features.channels.vm

import com.secondslot.coursework.domain.usecase.GetAllStreamsUseCase
import com.secondslot.coursework.domain.usecase.GetSubscribedStreamsUseCase
import com.secondslot.coursework.features.channels.core.StreamsListType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class StreamsListViewModelTest {

    lateinit var streamsListViewModel: StreamsListViewModel

    @Mock
    lateinit var getSubscribedStreamsUseCase: GetSubscribedStreamsUseCase

    @Mock
    lateinit var getAllStreamsUseCase: GetAllStreamsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        streamsListViewModel = StreamsListViewModel(
            getSubscribedStreamsUseCase,
            getAllStreamsUseCase
        )
    }

    @Test
    fun loadSubscribedStreams() {
        streamsListViewModel.loadStreams(StreamsListType.SUBSCRIBED)
        verify(getSubscribedStreamsUseCase).execute()
    }

    @Test
    fun loadAllStreams() {
        streamsListViewModel.loadStreams(StreamsListType.ALL_STREAMS)
        verify(getAllStreamsUseCase).execute()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Test
    fun observeSearchChanges() {
        val result = streamsListViewModel.observeSearchChanges(StreamsListType.SUBSCRIBED)
        assertThat(result, instanceOf(Flow::class.java))
    }
}
