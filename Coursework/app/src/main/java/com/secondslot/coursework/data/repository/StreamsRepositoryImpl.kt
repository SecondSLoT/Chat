package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.StreamRemoteToDomainMapper
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Observable

class StreamsRepositoryImpl : StreamsRepository {

    private val networkManager = NetworkManager()

    override fun getSubscribedStreams(): Observable<List<Stream>> {
        return networkManager.getSubscribedStreams().map { streamWithTopicsRemote ->
            StreamRemoteToDomainMapper.map(streamWithTopicsRemote)
        }
    }

    override fun getAllStreams(): Observable<List<Stream>> {
        return networkManager.getAllStreams().map { streamRemote ->
            StreamRemoteToDomainMapper.map(streamRemote)
        }
    }
}
