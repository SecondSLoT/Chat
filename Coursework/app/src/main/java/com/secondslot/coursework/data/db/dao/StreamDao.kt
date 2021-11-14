package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.secondslot.coursework.data.db.model.entity.StreamEntity
import io.reactivex.Completable

@Dao
interface StreamDao {

//    @Query("SELECT * FROM streams")
//    fun getAllStreams(): Single<List<StreamEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStreams(streams: List<StreamEntity>): Completable

    @Query("DELETE FROM streams WHERE is_subscribed = :isSubscribed")
    fun deleteStreams(isSubscribed: Boolean): Completable

//    @Query("DELETE FROM streams WHERE is_subscribed == 1")
//    fun deleteSubscribedStreams(): Completable
}
