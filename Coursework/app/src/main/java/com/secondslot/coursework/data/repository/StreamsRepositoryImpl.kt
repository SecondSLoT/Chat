package com.secondslot.coursework.data.repository

import android.util.Log
import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.StreamTopicsRemoteToStreamMapper
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.data.db.model.StreamWithTopicsDbToDomainMapper
import com.secondslot.coursework.data.db.model.entity.StreamEntity
import com.secondslot.coursework.data.db.model.entity.TopicToTopicEntityMapper
import com.secondslot.coursework.data.db.model.entity.toDomainModel
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class StreamsRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val networkManager: NetworkManager
) : StreamsRepository {

    override fun getSubscribedStreams(): Observable<List<Stream>> {

        // Data from DB
        val streamTopicDbObservable = database.streamWithTopicsDao.getStreamsTopics(true)
            .map { streamWithTopicsDbList ->
                Log.d(TAG, "SubscribedStreamsTopicsDb size = ${streamWithTopicsDbList.size}")
                StreamWithTopicsDbToDomainMapper.map(streamWithTopicsDbList)
            }.toObservable()

        // Data from network
        val streamTopicRemoteObservable = networkManager.getSubscribedStreams()
            .map { streamWithTopicsRemoteList ->
                StreamTopicsRemoteToStreamMapper.map(streamWithTopicsRemoteList, true)
            }
            // Save data from network to DB
            .flatMap { streams ->
                val streamEntities = streams.map { stream ->
                    StreamEntity.fromStream(stream, true)
                }
                val topicEntities = streams.flatMap { stream ->
                    TopicToTopicEntityMapper.map(stream.topics)
                }

                database.streamWithTopicsDao
                    .updateStreamsTopics(streamEntities, topicEntities, true)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnComplete { Log.d(TAG, "updateSubscribedStreamsWithTopics complete") }
                    .andThen(Observable.just(streams))
            }
            .doOnError { Log.e(TAG, "streamTopicObservableRemote error") }

        // Return data from DB first, then from network
        return Observable.concat(
            streamTopicDbObservable,
            streamTopicRemoteObservable
        )
    }

    override fun getAllStreams(): Observable<List<Stream>> {

        // Data from DB
        val streamTopicDbObservable = database.streamWithTopicsDao.getStreamsTopics(false)
            .map { streamWithTopicsDbList ->
                Log.d(TAG, "StreamsTopicsDb.size = ${streamWithTopicsDbList.size}")
                StreamWithTopicsDbToDomainMapper.map(streamWithTopicsDbList)
            }.toObservable()

        // Data from network
        val streamTopicRemoteObservable = networkManager.getAllStreams()
            .map { streamWithTopicsRemoteList ->
                StreamTopicsRemoteToStreamMapper.map(streamWithTopicsRemoteList, false)
            }
            // Save data from network to DB
            .flatMap { streams ->
                val streamEntities = streams.map { stream ->
                    StreamEntity.fromStream(stream, false)
                }
                val topicEntities = streams.flatMap { stream ->
                    TopicToTopicEntityMapper.map(stream.topics)
                }

                database.streamWithTopicsDao
                    .updateStreamsTopics(streamEntities, topicEntities, false)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnComplete { Log.d(TAG, "updateAllStreamsWithTopics complete") }
                    .andThen(Observable.just(streams))
            }

        // Return data from DB first, then from network
        return Observable.concat(
            streamTopicDbObservable,
            streamTopicRemoteObservable
        )
    }

    override fun getStreamById(streamId: Int): Observable<Stream> {

        // Data from DB
        return database.streamDao.getStream(streamId)
            .map { it.toDomainModel() }
            .toObservable()
    }

    companion object {
        private const val TAG = "StreamsRepositoryImpl"
    }
}
