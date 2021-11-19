package com.secondslot.coursework.data.repository

import android.util.Log
import com.secondslot.coursework.App
import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.StreamTopicsRemoteToStreamMapper
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.data.db.model.StreamWithTopicsDbToDomainMapper
import com.secondslot.coursework.data.db.model.entity.StreamEntity
import com.secondslot.coursework.data.db.model.entity.TopicEntity
import com.secondslot.coursework.data.db.model.entity.TopicToTopicEntityMapper
import com.secondslot.coursework.di.GlobalDI
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class StreamsRepositoryImpl : StreamsRepository {

    private val database: AppDatabase = GlobalDI.INSTANCE.appDatabase
    private val networkManager = NetworkManager()

    override fun getSubscribedStreams(): Observable<List<Stream>> {

        // Data from DB
        val streamTopicDbObservable = database.streamWithTopicsDao
            .getStreamsTopics(true).map { streamWithTopicsDbList ->
                Log.d(TAG, "SubscribedStreamsTopicsDb size = ${streamWithTopicsDbList.size}")
                StreamWithTopicsDbToDomainMapper.map(streamWithTopicsDbList)
            }.toObservable()

        // Data from network
        val streamTopicRemoteObservable = networkManager
            .getSubscribedStreams().map { streamWithTopicsRemoteList ->
                StreamTopicsRemoteToStreamMapper.map(streamWithTopicsRemoteList, true)
            }

        // Save data from network to DB
        val disposable = streamTopicRemoteObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onNext = { streams ->
                    val streamEntities: ArrayList<StreamEntity> = arrayListOf()
                    val topicEntities: ArrayList<TopicEntity> = arrayListOf()
                    streams.forEach { stream ->
                        // Set "isSubscribed" to true for adding streamEntities
                        streamEntities.add(StreamEntity.fromStream(stream, true))
                        topicEntities.addAll(TopicToTopicEntityMapper.map(stream.topics))
                    }

                    database.streamWithTopicsDao
                        .updateStreamsTopics(streamEntities, topicEntities, true)
                },
                onError = { Log.e(TAG, "streamTopicObservableRemote error") }
            )

        // Return data from DB first, then from network
        return Observable.concat(
            streamTopicDbObservable,
            streamTopicRemoteObservable
        )
    }

    override fun getAllStreams(): Observable<List<Stream>> {

        // Data from DB
        val streamTopicDbObservable = database.streamWithTopicsDao
            .getStreamsTopics(false).map { streamWithTopicsDbList ->
                Log.d(TAG, "UnsubscribedStreamsTopicsDb.size = ${streamWithTopicsDbList.size}")
                StreamWithTopicsDbToDomainMapper.map(streamWithTopicsDbList)
            }.toObservable()

        // Data from network
        val streamTopicRemoteObservable = networkManager
            .getAllStreams().map { streamWithTopicsRemoteList ->
                StreamTopicsRemoteToStreamMapper.map(streamWithTopicsRemoteList, false)
            }

        // Save data from network to DB
        val disposable = streamTopicRemoteObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onNext = { streams ->
                    val streamEntities: ArrayList<StreamEntity> = arrayListOf()
                    val topicEntities: ArrayList<TopicEntity> = arrayListOf()
                    streams.forEach { stream ->
                        streamEntities.add(StreamEntity.fromStream(stream, false))
                        topicEntities.addAll(TopicToTopicEntityMapper.map(stream.topics))
                    }

                    database.streamWithTopicsDao
                        .updateStreamsTopics(streamEntities, topicEntities, false)
                },
                onError = { Log.e(TAG, "streamTopicRemoteObservable error") }
            )

        // Return data from DB first, then from network
        return Observable.concat(
            streamTopicDbObservable,
            streamTopicRemoteObservable
        )
    }

    companion object {
        private const val TAG = "StreamsRepositoryImpl"
    }
}
