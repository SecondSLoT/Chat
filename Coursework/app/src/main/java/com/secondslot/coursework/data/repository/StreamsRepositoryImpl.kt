package com.secondslot.coursework.data.repository

import android.util.Log
import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.StreamTopicsRemoteToStreamMapper
import com.secondslot.coursework.data.api.model.toDomainModel
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.data.db.model.StreamWithTopicsDbToDomainMapper
import com.secondslot.coursework.data.db.model.entity.StreamEntity
import com.secondslot.coursework.data.db.model.entity.TopicEntity
import com.secondslot.coursework.data.db.model.entity.TopicToTopicEntityMapper
import com.secondslot.coursework.data.db.model.toDomainModel
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StreamsRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val networkManager: NetworkManager
) : StreamsRepository {

    override fun getSubscribedStreams(): Flow<List<Stream>> = flow {

        // Data from DB
        val streamTopicFromDb = database.streamWithTopicsDao
            .getStreamsTopics(true).map { it.toDomainModel() }
        Log.d(TAG, "SubscribedStreamsTopicsDb size = ${streamTopicFromDb.size}")
        emit(streamTopicFromDb)

        // Data from network
        val streamTopicFromNetwork = networkManager
            .getSubscribedStreams().map { it.toDomainModel(true) }
        emit(streamTopicFromNetwork)

        // Save data from network to DB
        val streamEntities: ArrayList<StreamEntity> = arrayListOf()
        val topicEntities: ArrayList<TopicEntity> = arrayListOf()
        streamTopicFromNetwork.forEach { stream ->
            // Set "isSubscribed" to true for adding streamEntities
            streamEntities.add(StreamEntity.fromStream(stream, true))
            topicEntities.addAll(TopicToTopicEntityMapper.map(stream.topics))
        }

        database.streamWithTopicsDao
            .updateStreamsTopics(streamEntities, topicEntities, true)
    }

    override fun getAllStreams(): Flow<List<Stream>> = flow {

        // Data from DB
        val streamTopicFromDb = database.streamWithTopicsDao
            .getStreamsTopics(false).map { it.toDomainModel()  }
        Log.d(TAG, "UnsubscribedStreamsTopicsDb.size = ${streamTopicFromDb.size}")
        emit(streamTopicFromDb)

        // Data from network
        val streamTopicFromNetwork =
            networkManager.getAllStreams().map { it.toDomainModel(false) }
        emit(streamTopicFromNetwork)

        // Save data from network to DB
        val streamEntities: ArrayList<StreamEntity> = arrayListOf()
        val topicEntities: ArrayList<TopicEntity> = arrayListOf()
        streamTopicFromNetwork.forEach { stream ->
            streamEntities.add(StreamEntity.fromStream(stream, false))
            topicEntities.addAll(TopicToTopicEntityMapper.map(stream.topics))
        }

        database.streamWithTopicsDao
            .updateStreamsTopics(streamEntities, topicEntities, false)
    }

    companion object {
        private const val TAG = "StreamsRepositoryImpl"
    }
}
