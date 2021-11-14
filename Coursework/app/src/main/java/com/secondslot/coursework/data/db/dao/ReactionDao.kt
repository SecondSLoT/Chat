package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.secondslot.coursework.data.db.model.entity.ReactionEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface ReactionDao {

    @Query("SELECT * FROM reactions WHERE message_id == :messageId")
    fun getReactions(messageId: Int): Single<List<ReactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReactions(reactions: List<ReactionEntity>): Completable

    @Query("DELETE FROM reactions WHERE message_id == :messageId")
    fun deleteReactions(messageId: String): Completable
}
