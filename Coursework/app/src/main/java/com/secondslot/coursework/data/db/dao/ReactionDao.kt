package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.secondslot.coursework.data.db.model.entity.ReactionEntity
import io.reactivex.Completable

@Dao
interface ReactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReactions(reactions: List<ReactionEntity>): Completable
}
