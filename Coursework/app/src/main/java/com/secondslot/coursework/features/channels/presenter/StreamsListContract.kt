package com.secondslot.coursework.features.channels.presenter

import com.secondslot.coursework.base.mvp.presenter.Presenter
import com.secondslot.coursework.features.channels.model.ExpandableStreamModel

interface StreamsListContract {

    interface StreamsListView {

        fun getViewType(): String

        fun setStateLoading()

        fun setStateResult(expandableStreamModel: List<ExpandableStreamModel>)

        fun setStateError(error: Throwable)
    }

    interface StreamsListPresenter : Presenter<StreamsListView> {

        fun searchStreams(searchQuery: String)

        fun retry()
    }

    companion object {
        const val SUBSCRIBED = "subscribed"
        const val ALL_STREAMS = "all_streams"
    }
}
