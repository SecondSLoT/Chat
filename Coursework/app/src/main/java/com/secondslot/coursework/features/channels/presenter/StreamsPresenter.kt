package com.secondslot.coursework.features.channels.presenter

import com.secondslot.coursework.base.mvp.presenter.RxPresenter
import com.secondslot.coursework.features.channels.ui.SearchQueryListener
import com.secondslot.coursework.features.channels.ui.StreamsListFragment
import com.secondslot.coursework.features.channels.ui.StreamsListView
import com.secondslot.coursework.features.channels.ui.StreamsView
import javax.inject.Inject

class StreamsPresenter @Inject constructor() : RxPresenter<StreamsView>() {

    private var streamsFragmentsList = listOf(
        StreamsListFragment.newInstance(StreamsListView.SUBSCRIBED),
        StreamsListFragment.newInstance(StreamsListView.ALL_STREAMS)
    )

    override fun attachView(view: StreamsView) {
        super.attachView(view)
        view.initViews(streamsFragmentsList)
    }

    fun searchStreams(currentPosition: Int, searchQuery: String) {
        (streamsFragmentsList[currentPosition] as SearchQueryListener).search(searchQuery)
    }

    companion object {
        private const val TAG = "StreamsPresenter"
    }
}
