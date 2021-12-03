package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.secondslot.coursework.data.db.model.entity.StreamEntity

@Dao
interface StreamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<StreamEntity>)

    @Query("DELETE FROM streams WHERE is_subscribed == :isSubscribed")
    suspend fun deleteStreams(isSubscribed: Boolean)

    @Query("SELECT * FROM streams WHERE id == :streamId")
    suspend fun getStream(streamId: Int): StreamEntity
}
