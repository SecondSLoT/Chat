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
import io.reactivex.schedulers.Schedulers

@Dao
abstract class StreamWithTopicsDao : StreamDao, TopicDao {

    @Transaction
    @Query("SELECT * FROM streams WHERE is_subscribed == :isSubscribed")
    abstract fun getStreamsTopics(isSubscribed: Boolean): Single<List<StreamWithTopicsDb>>

    private fun insertStreamsTopics(
        streams: List<StreamEntity>, topics: List<TopicEntity>
    ): Completable {

        return insertStreams(streams).subscribeOn(Schedulers.io())
                .doOnError { Log.d(TAG, "insertStreams error") }
                .doOnComplete { Log.d(TAG, "insertStreams complete") }
                .andThen(insertTopics(topics).subscribeOn(Schedulers.io()))
    }

    private fun deleteStreamsTopics(isSubscribed: Boolean): Completable {
        return deleteStreams(isSubscribed)
        // Topics will be deleted automatically because they have "stream_id" as a foreign key
    }

    fun updateStreamsTopics(
        streams: List<StreamEntity>,
        topics: List<TopicEntity>,
        isSubscribed: Boolean = false
    ): Completable {

        return deleteStreamsTopics(isSubscribed).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .andThen(insertStreamsTopics(streams, topics).subscribeOn(Schedulers.io()))
    }

    companion object {
        private const val TAG = "StreamWithTopicsDao"
    }
}
