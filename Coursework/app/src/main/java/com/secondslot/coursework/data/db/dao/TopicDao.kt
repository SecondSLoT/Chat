package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.secondslot.coursework.data.db.model.entity.TopicEntity
import io.reactivex.Completable

@Dao
interface TopicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopics(topics: List<TopicEntity>): Completable
}
