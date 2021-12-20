package com.secondslot.coursework.features.channels.ui

import androidx.fragment.app.Fragment
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface StreamsView : MvpView {

    @AddToEndSingle
    fun initViews(streamsFragmentsList: List<Fragment>)

    @AddToEndSingle
    fun clearSearchView()
}
