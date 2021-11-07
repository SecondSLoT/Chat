package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.api.Fetcher
import com.secondslot.coursework.data.api.model.RemoteToDomainMapper
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Observable

class StreamsRepositoryImpl : StreamsRepository {

    private val fetcher = Fetcher()

    override fun getSubscribedStreams(): Observable<List<Stream>> {
        return fetcher.getSubscribedStreams().map { streamWithTopicsRemote ->
            RemoteToDomainMapper.map(streamWithTopicsRemote)
        }
    }

    override fun getAllStreams(): Observable<List<Stream>> {
        return fetcher.getSubscribedStreams().map { streamRemote ->
            RemoteToDomainMapper.map(streamRemote)
        }
    }
}
