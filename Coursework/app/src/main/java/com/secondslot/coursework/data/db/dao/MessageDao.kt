package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.secondslot.coursework.data.db.model.entity.MessageEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MessageDao {

//    @Query("SELECT * FROM messages WHERE topic_name == :topicName")
//    fun getMessages(topicName: String): Single<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessages(messages: List<MessageEntity>): Completable

    @Query("DELETE FROM messages WHERE topic_name == :topicName")
    fun deleteMessages(topicName: String): Completable
}
