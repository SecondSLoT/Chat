package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.domain.model.Stream
import kotlinx.coroutines.flow.Flow

interface StreamsRepository {

    fun getSubscribedStreams(): Flow<List<Stream>>

    fun getAllStreams(): Flow<List<Stream>>

    suspend fun getStreamById(streamId: Int): Stream
}
