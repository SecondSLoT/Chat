package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.secondslot.coursework.data.db.model.entity.StreamEntity
import io.reactivex.Completable

@Dao
interface StreamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStreams(streams: List<StreamEntity>): Completable

    @Query("DELETE FROM streams WHERE is_subscribed == :isSubscribed")
    fun deleteStreams(isSubscribed: Boolean): Completable
}
