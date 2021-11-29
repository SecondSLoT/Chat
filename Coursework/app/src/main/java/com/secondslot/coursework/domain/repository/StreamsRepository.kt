package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.domain.model.Stream
import io.reactivex.Observable

interface StreamsRepository {

    fun getSubscribedStreams(): Observable<List<Stream>>

    fun getAllStreams(): Observable<List<Stream>>

    fun getStreamById(streamId: Int): Observable<Stream>
}
