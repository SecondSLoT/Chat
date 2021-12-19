package com.secondslot.coursework.features.channels.ui

import com.secondslot.coursework.features.channels.model.ExpandableStreamModel

interface StreamsListView {

    fun setStateLoading()

    fun setStateResult(expandableStreamModel: List<ExpandableStreamModel>)

    fun setStateError(error: Throwable)

    fun openChat(topicName: String, maxMessageId: Int, streamId: Int)

    fun openCreateStreamDialog()

    fun submitStreamsList(expandableStreamModel: List<ExpandableStreamModel>)

    companion object {
        const val SUBSCRIBED = "subscribed"
        const val ALL_STREAMS = "all_streams"
    }
}
