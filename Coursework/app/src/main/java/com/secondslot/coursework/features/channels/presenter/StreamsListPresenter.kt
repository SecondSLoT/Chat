package com.secondslot.coursework.features.channels.presenter

import android.util.Log
import com.secondslot.coursework.base.mvp.presenter.RxPresenter
import com.secondslot.coursework.domain.interactor.StreamInteractor
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.features.channels.model.ExpandableStreamModel
import com.secondslot.coursework.features.channels.ui.StreamsListView
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class StreamsListPresenter @AssistedInject constructor(
    private val streamInteractor: StreamInteractor,
    @Assisted private val viewType: String
) : RxPresenter<StreamsListView>() {

    private var streamsCache = mutableListOf<ExpandableStreamModel>()
    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    override fun attachView(view: StreamsListView) {
        super.attachView(view)
        Log.d(TAG, "$this AttachView() $view")
        loadStreams()
        subscribeOnSearchChanges()
    }

    override fun detachView(isFinishing: Boolean) {
        Log.d(TAG, "DetachView()  $view")
        super.detachView(isFinishing)
    }

    private fun loadStreams() {
        val streamsObservable: Observable<List<Stream>> =
            when (viewType) {
                StreamsListView.SUBSCRIBED -> streamInteractor.getSubscribedStreams()
                else -> streamInteractor.getAllStreams()
            }

        streamsObservable
            .subscribeOn(Schedulers.io())
            .map { ExpandableStreamModel.fromStream(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { newStreams ->
                    Log.d(TAG, "streamsObservable onNext")
                    if (newStreams.isNullOrEmpty()) {
                        view?.setStateLoading()
                    } else {
                        val resultList = mergeStreams(streamsCache, newStreams)
                        view?.setStateResult(resultList)
                        streamsCache = resultList.toMutableList()
                    }
                },
                onError = { view?.setStateError(it) }
            )
            .disposeOnFinish()
    }

    private fun mergeStreams(
        oldList: List<ExpandableStreamModel>,
        newList: List<ExpandableStreamModel>
    ): List<ExpandableStreamModel> {

        (newList as ArrayList).map { newStream ->
            val oldStream = oldList.find { it.stream.id == newStream.stream.id }
            if (oldStream != null) newStream.isExpanded = oldStream.isExpanded
                newStream
        }
            .forEach { expandableStreamModel ->
                if (expandableStreamModel.isExpanded &&
                    expandableStreamModel.type == ExpandableStreamModel.PARENT
                ) {
                    var nextPosition = newList.indexOf(expandableStreamModel)
                    for (child in expandableStreamModel.stream.topics) {
                        newList.add(
                            ++nextPosition,
                            ExpandableStreamModel(ExpandableStreamModel.CHILD, child)
                        )
                    }
                }
            }

        return newList
    }

    private fun subscribeOnSearchChanges() {
        searchSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { view?.setStateLoading() }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { searchQuery ->
                when (viewType) {
                    StreamsListView.SUBSCRIBED -> {
                        streamInteractor.getSubscribedStreams(searchQuery)
                    }
                    else -> {
                        streamInteractor.getAllStreams(searchQuery)
                    }
                }
            }
            .map { ExpandableStreamModel.fromStream(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { newStreams ->
                    val resultList = mergeStreams(streamsCache, newStreams)
                    view?.setStateResult(resultList)
                    streamsCache = resultList.toMutableList()
                },
                onError = { view?.setStateError(it) }
            )
            .disposeOnFinish()
    }

    fun searchStreams(searchQuery: String) {
        searchSubject.onNext(searchQuery)
    }

    fun onExpandRow(position: Int) {
        streamsCache[position].isExpanded = true
        val row = streamsCache[position]
        var nextPosition = position

        if (row.type == ExpandableStreamModel.PARENT) {
            for (child in row.stream.topics) {
                streamsCache.add(
                    ++nextPosition,
                    ExpandableStreamModel(ExpandableStreamModel.CHILD, child)
                )
            }
        }

        view?.submitStreamsList(streamsCache.toList())
    }

    fun onCollapseRow(position: Int) {
        streamsCache[position].isExpanded = false
        val row = streamsCache[position]
        val nextPosition = position + 1

        if (row.type == ExpandableStreamModel.PARENT) {
            outerloop@ while (true) {
                if (nextPosition == streamsCache.size ||
                    streamsCache[nextPosition].type == ExpandableStreamModel.PARENT
                ) {
                    break@outerloop
                }

                streamsCache.removeAt(nextPosition)
            }

            view?.submitStreamsList(streamsCache.toList())
        }
    }

    fun onRetryClicked() {
        loadStreams()
        subscribeOnSearchChanges()
    }

    fun onTopicClicked(topicName: String, maxMessageId: Int, streamId: Int) {
        view?.openChat(topicName, maxMessageId, streamId)
    }

    fun onFabClicked() {
        view?.openCreateStreamDialog()
    }

    fun onCreateNewStream(streamName: String, description: String) {
        Log.d(TAG, "Create new stream: $streamName, $description")
        val subscriptions = mapOf<String, Any>(
            "name" to streamName,
            "description" to description
        )
        streamInteractor.createOrSubscribeOnStream(subscriptions)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    Log.d(TAG, "Create stream: ${it.result}")
                    searchStreams("")
                },
                onError = { Log.d(TAG, "Error: ${it.message}") }
            )
            .disposeOnFinish()
    }

    companion object {
        private const val TAG = "StreamsListPresenter"
    }
}
