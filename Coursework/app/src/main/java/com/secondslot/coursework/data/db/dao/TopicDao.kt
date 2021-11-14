package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.secondslot.coursework.data.db.model.entity.TopicEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TopicDao {

//    @Query("SELECT * FROM topics WHERE stream_id == :streamId")
//    fun getTopics(streamId: Int): Single<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTopics(topics: List<TopicEntity>): Completable

//    @Query("DELETE FROM topics")
//    fun deleteAllTopics(): Completable

//    @Query("DELETE FROM topics WHERE stream_id == :streamId")
//    fun deleteTopics(streamId: Int): Completable
}
