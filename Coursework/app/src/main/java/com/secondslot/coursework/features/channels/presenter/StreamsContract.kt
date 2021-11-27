package com.secondslot.coursework.features.channels.presenter

import androidx.fragment.app.Fragment
import com.secondslot.coursework.base.mvp.presenter.Presenter

interface StreamsContract {

    interface StreamsView {

        fun clearSearchView()

    }

    interface StreamsPresenter : Presenter<StreamsView> {

        fun getStreamsFragments(): List<Fragment>

        fun onPageChanged()

        fun searchStreams(currentPosition: Int, searchQuery: String)
    }
}
