package com.secondslot.coursework.features.channels.ui

import com.secondslot.coursework.features.channels.model.ExpandableStreamModel
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.SingleState

interface StreamsListView : MvpView {

    @SingleState
    fun setStateLoading()

    @SingleState
    fun setStateResult(expandableStreamModel: List<ExpandableStreamModel>)

    @SingleState
    fun setStateError(error: Throwable)

    @OneExecution
    fun openChat(topicName: String, maxMessageId: Int, streamId: Int)

    @OneExecution
    fun showFab(isShown: Boolean)

    @AddToEndSingle
    fun openCreateStreamDialog()

    @AddToEndSingle
    fun submitStreamsList(expandableStreamModel: List<ExpandableStreamModel>)

    companion object {
        const val SUBSCRIBED = "subscribed"
        const val ALL_STREAMS = "all_streams"
    }
}
