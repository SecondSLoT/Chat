package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.data.api.model.response.ServerResponse
import com.secondslot.coursework.domain.model.Stream
import io.reactivex.Observable
import io.reactivex.Single

interface StreamsRepository {

    fun getSubscribedStreams(): Observable<List<Stream>>

    fun getAllStreams(): Observable<List<Stream>>

    fun getStreamById(streamId: Int): Observable<Stream>

    fun createOrSubscribeOnStream(
        subscriptions: Map<String, Any>,
        announce: Boolean
    ): Single<ServerResponse>

    fun getTopics(streamId: Int): Observable<List<Stream.Topic>>
}
