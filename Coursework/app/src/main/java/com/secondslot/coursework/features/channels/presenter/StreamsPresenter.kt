package com.secondslot.coursework.features.channels.presenter

import androidx.fragment.app.Fragment
import com.secondslot.coursework.base.mvp.presenter.RxPresenter
import com.secondslot.coursework.features.channels.ui.SearchQueryListener
import com.secondslot.coursework.features.channels.ui.StreamsListFragment
import javax.inject.Inject

class StreamsPresenter @Inject constructor() :
    RxPresenter<StreamsContract.StreamsView>(StreamsContract.StreamsView::class.java),
    StreamsContract.StreamsPresenter {

    private var streamsFragmentsList = listOf(
        StreamsListFragment.newInstance(StreamsListContract.SUBSCRIBED),
        StreamsListFragment.newInstance(StreamsListContract.ALL_STREAMS)
    )

    override fun getStreamsFragments(): List<Fragment> {
        return streamsFragmentsList
    }

    override fun onPageChanged() {
        view?.clearSearchView()
    }

    override fun searchStreams(currentPosition: Int, searchQuery: String) {
        (streamsFragmentsList[currentPosition] as SearchQueryListener).search(searchQuery)
    }

    companion object {
        private const val TAG = "StreamsPresenter"
    }
}
