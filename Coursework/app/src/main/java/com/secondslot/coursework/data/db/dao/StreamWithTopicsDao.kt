package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.secondslot.coursework.data.db.model.StreamWithTopicsDb
import com.secondslot.coursework.data.db.model.entity.StreamEntity
import com.secondslot.coursework.data.db.model.entity.TopicEntity

@Dao
abstract class StreamWithTopicsDao : StreamDao, TopicDao {

    @Transaction
    @Query("SELECT * FROM streams WHERE is_subscribed == :isSubscribed")
    abstract suspend fun getStreamsTopics(isSubscribed: Boolean): List<StreamWithTopicsDb>

    private suspend fun insertStreamsTopics(
        streams: List<StreamEntity>,
        topics: List<TopicEntity>
    ) {
        insertStreams(streams)
        insertTopics(topics)
    }

    private suspend fun deleteStreamsTopics(isSubscribed: Boolean) {
        deleteStreams(isSubscribed)
        // Topics will be deleted automatically because they have "stream_id" as a foreign key
    }

    suspend fun updateStreamsTopics(
        streams: List<StreamEntity>,
        topics: List<TopicEntity>,
        isSubscribed: Boolean = false
    ) {
        // Delete streams with topics
        deleteStreamsTopics(isSubscribed)
        // Insert given streams with topics
        insertStreamsTopics(streams, topics)
    }

    companion object {
        private const val TAG = "StreamWithTopicsDao"
    }
}
