package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.secondslot.coursework.data.db.model.entity.ReactionEntity

@Dao
interface ReactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReactions(reactions: List<ReactionEntity>)
}
