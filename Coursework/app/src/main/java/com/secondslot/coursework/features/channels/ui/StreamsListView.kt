package com.secondslot.coursework.features.channels.ui

import com.secondslot.coursework.features.channels.model.ExpandableStreamModel

interface StreamsListView {

    fun setStateLoading()

    fun setStateResult(expandableStreamModel: List<ExpandableStreamModel>)

    fun setStateError(error: Throwable)

    companion object {
        const val SUBSCRIBED = "subscribed"
        const val ALL_STREAMS = "all_streams"
    }
}
