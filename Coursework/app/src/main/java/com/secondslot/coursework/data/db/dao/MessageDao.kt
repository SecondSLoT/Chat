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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessages(messages: List<MessageEntity>): Completable

    @Query("SELECT * FROM messages WHERE id == :messageId")
    fun getMessage(messageId: Int): Single<MessageEntity>

    @Query("DELETE FROM messages WHERE topic_name == :topicName")
    fun deleteMessages(topicName: String): Completable

    @Query("DELETE FROM messages WHERE id == :messageId")
    fun deleteMessage(messageId: Int): Completable

    @Query("UPDATE messages SET content = :newMessageText WHERE id == :messageId")
    fun editMessage(messageId: Int, newMessageText: String): Completable
}
