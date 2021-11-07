package com.secondslot.coursework.data.api

import android.util.Log
import com.secondslot.coursework.data.api.model.StreamWithTopicsRemote
import io.reactivex.Observable


class Fetcher {

    private val apiService = ZulipApiService.create()

    fun getSubscribedStreams(): Observable<List<StreamWithTopicsRemote>> {

        return apiService.getSubscribedStreams()
            // Convert StreamsResponse to List<StreamRemote>
            .map{ it.streams }
            // Convert List<StreamRemote> to StreamRemote
            .flatMap { streamRemoteList -> Observable.fromIterable(streamRemoteList)}
            .flatMap(
                // Load List<TopicRemote> for certain StreamRemote
                { streamRemote -> apiService.getTopics(streamRemote.id).map { it.topics } },
                // Create StreamWithTopicsRemote and set StreamRemote and List<TopicRemote> to it
                { streamRemote, topics ->
                    StreamWithTopicsRemote(
                        streamRemote = streamRemote,
                        topics = topics
                    )
                }
            )
            // Gather all StreamWithTopicsRemote in one list
            .toList()
            // Convert Single<List<StreamWithTopicsRemote>>
            // to Observable<List<StreamWithTopicsRemote>>
            .toObservable()
    }

    companion object {
        private const val TAG = "Fetcher"
    }
}
