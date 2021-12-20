package com.secondslot.coursework.features.channels.presenter

import com.secondslot.coursework.base.mvp.MoxyRxPresenter
import com.secondslot.coursework.features.channels.ui.SearchQueryListener
import com.secondslot.coursework.features.channels.ui.StreamsListFragment
import com.secondslot.coursework.features.channels.ui.StreamsListView
import com.secondslot.coursework.features.channels.ui.StreamsView
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class StreamsPresenter @Inject constructor() : MoxyRxPresenter<StreamsView>() {

    private var streamsFragmentsList = listOf(
        StreamsListFragment.newInstance(StreamsListView.SUBSCRIBED),
        StreamsListFragment.newInstance(StreamsListView.ALL_STREAMS)
    )

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.initViews(streamsFragmentsList)
    }

    fun searchStreams(currentPosition: Int, searchQuery: String) {
        (streamsFragmentsList[currentPosition] as SearchQueryListener).search(searchQuery)
    }

    companion object {
        private const val TAG = "StreamsPresenter"
    }
}
