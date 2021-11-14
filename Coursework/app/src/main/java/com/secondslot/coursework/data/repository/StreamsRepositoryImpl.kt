package com.secondslot.coursework.data.repository

import android.util.Log
import com.secondslot.coursework.App
import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.StreamRemoteToDomainMapper
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.data.db.model.StreamWithTopicsDbToDomainMapper
import com.secondslot.coursework.data.db.model.entity.StreamEntity
import com.secondslot.coursework.data.db.model.entity.TopicEntity
import com.secondslot.coursework.data.db.model.entity.TopicToTopicEntityMapper
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class StreamsRepositoryImpl : StreamsRepository {

    private val database: AppDatabase = App.getAppDatabase()

    private val networkManager = NetworkManager()

    override fun getSubscribedStreams(): Observable<List<Stream>> {

        // Data from DB
        val streamTopicObservableDb = database.streamWithTopicsDao
            .getSubscribedStreamsTopics().map { streamWithTopicsDbList ->
                Log.d(TAG, "SubscribedStreamsTopicsDb.size = ${streamWithTopicsDbList.size}")
                StreamWithTopicsDbToDomainMapper.map(streamWithTopicsDbList)
            }.toObservable()

        // Data from network
        val streamTopicObservableRemote = networkManager
            .getSubscribedStreams().map { streamWithTopicsRemoteList ->
                StreamRemoteToDomainMapper.map(streamWithTopicsRemoteList)
            }

        // Save data from network to DB
        val disposable = streamTopicObservableRemote.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onNext = { streams ->
                    val streamEntities: ArrayList<StreamEntity> = arrayListOf()
                    val topicEntities: ArrayList<TopicEntity> = arrayListOf()
                    streams.forEach { stream ->
                        // Set "isSubscribed" to true for adding streamEntities
                        streamEntities.add(StreamEntity.fromDomainModel(stream, true))
                        topicEntities.addAll(TopicToTopicEntityMapper.map(stream.topics))
                    }

                    database.streamWithTopicsDao
                        .updateStreamsTopics(streamEntities, topicEntities, true)
                },
                onError = { Log.e(TAG, "streamTopicObservableRemote error") }
            )

        // Return data from DB first, then from network
        return Observable.concat(
            streamTopicObservableDb,
            streamTopicObservableRemote
        )
    }

    override fun getAllStreams(): Observable<List<Stream>> {

        // Data from DB
        val streamTopicObservableDb = database.streamWithTopicsDao
            .getAllStreamsTopics().map { streamWithTopicsDbList ->
                Log.d(TAG, "UnsubscribedStreamsTopicsDb.size = ${streamWithTopicsDbList.size}")
                StreamWithTopicsDbToDomainMapper.map(streamWithTopicsDbList)
            }.toObservable()

        // Data from network
        val streamTopicObservableRemote = networkManager
            .getAllStreams().map { streamWithTopicsRemoteList ->
                StreamRemoteToDomainMapper.map(streamWithTopicsRemoteList)
            }

//        // Save data from network to DB
//        val disposable = streamTopicObservableRemote.subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.io())
//            .subscribeBy(
//                onNext = { streams ->
//                    val streamEntities: ArrayList<StreamEntity> = arrayListOf()
//                    val topicEntities: ArrayList<TopicEntity> = arrayListOf()
//                    streams.forEach { stream ->
//                        streamEntities.add(StreamEntity.fromDomainModel(stream))
//                        topicEntities.addAll(TopicToTopicEntityMapper.map(stream.topics))
//                    }
//
//                    database.streamWithTopicsDao
//                        .updateStreamsTopics(streamEntities, topicEntities, false)
//                },
//                onError = { Log.e(TAG, "streamTopicObservableRemote error") }
//            )

        // Return data from DB first, then from network
        return Observable.concat(
            streamTopicObservableDb,
            streamTopicObservableRemote
        )
    }

    companion object {
        private const val TAG = "StreamsRepositoryImpl"
    }
}
