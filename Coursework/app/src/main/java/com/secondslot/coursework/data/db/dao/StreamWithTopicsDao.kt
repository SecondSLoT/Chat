package com.secondslot.coursework.data.db.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.secondslot.coursework.data.db.model.StreamWithTopicsDb
import com.secondslot.coursework.data.db.model.entity.StreamEntity
import com.secondslot.coursework.data.db.model.entity.TopicEntity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

@Dao
abstract class StreamWithTopicsDao : StreamDao, TopicDao {

    @Transaction
    @Query("SELECT * FROM streams")
    abstract fun getAllStreamsTopics(): Single<List<StreamWithTopicsDb>>

    @Transaction
    @Query("SELECT * FROM streams WHERE is_subscribed == 1")
    abstract fun getSubscribedStreamsTopics(): Single<List<StreamWithTopicsDb>>

    private fun insertStreamsTopics(streams: List<StreamEntity>, topics: List<TopicEntity>) {
        val streamsCompletable = insertStreams(streams)
        val topicsCompletable = insertTopics(topics)

        val streamsDisposable = streamsCompletable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onComplete = { Log.d(TAG, "insertStreams complete") },
                onError = { Log.d(TAG, "insertStreams error") }
            )

        val topicsDiposable = topicsCompletable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onComplete = { Log.d(TAG, "insertTopics complete") },
                onError = { Log.d(TAG, "insertTopics error") }
            )
    }

    private fun deleteStreamsTopics(isSubscribed: Boolean): Completable {
        return deleteStreams(isSubscribed)
        // Topics will be deleted automatically because they have "stream_id" as a foreign key
    }

    fun updateStreamsTopics(
        streams: List<StreamEntity>,
        topics: List<TopicEntity>,
        isSubscribed: Boolean = false
    ) {

//        // Delete streams with topics
//        val deleteCompletable = deleteStreamsTopics(isSubscribed)
//
//        // Insert given streams with topics
//        val disposable = deleteCompletable.subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.io())
//            .subscribeBy(
//                onComplete = { insertStreamsTopics(streams, topics) }
//            )
        insertStreamsTopics(streams, topics)
    }

    companion object {
        private const val TAG = "StreamWithTopicsDao"
    }
}
