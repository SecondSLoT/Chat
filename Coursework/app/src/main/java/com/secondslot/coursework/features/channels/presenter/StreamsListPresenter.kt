package com.secondslot.coursework.features.channels.presenter

import android.util.Log
import com.secondslot.coursework.base.mvp.presenter.RxPresenter
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.usecase.stream.CreateOrSubscribeOnStreamUseCase
import com.secondslot.coursework.domain.usecase.stream.GetAllStreamsUseCase
import com.secondslot.coursework.domain.usecase.stream.GetSubscribedStreamsUseCase
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
    private val getSubscribedStreamsUseCase: GetSubscribedStreamsUseCase,
    private val getAllStreamsUseCase: GetAllStreamsUseCase,
    private val createOrSubscribeOnStreamUseCase: CreateOrSubscribeOnStreamUseCase,
    @Assisted private val viewType: String
) : RxPresenter<StreamsListView>() {

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
                StreamsListView.SUBSCRIBED -> getSubscribedStreamsUseCase.execute()
                else -> getAllStreamsUseCase.execute()
            }

        streamsObservable
            .subscribeOn(Schedulers.io())
            .map { ExpandableStreamModel.fromStream(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Log.d(TAG, "streamsObservable onNext")
                    if (it.isNullOrEmpty()) {
                        view?.setStateLoading()
                    } else {
                        view?.setStateResult(it)
                    }
                },
                onError = { view?.setStateError(it) }
            )
            .disposeOnFinish()
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
                        getSubscribedStreamsUseCase.execute(searchQuery)
                            .map { ExpandableStreamModel.fromStream(it) }
                    }
                    else -> {
                        getAllStreamsUseCase.execute(searchQuery)
                            .map { ExpandableStreamModel.fromStream(it) }
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { view?.setStateResult(it) },
                onError = { view?.setStateError(it) }
            )
            .disposeOnFinish()
    }

    fun searchStreams(searchQuery: String) {
        searchSubject.onNext(searchQuery)
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
        createOrSubscribeOnStreamUseCase.execute(subscriptions)
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
