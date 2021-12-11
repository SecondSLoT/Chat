package com.secondslot.coursework.features.channels.ui

import androidx.fragment.app.Fragment

interface StreamsView {

    fun initViews(streamsFragmentsList: List<Fragment>)

    fun clearSearchView()
}
