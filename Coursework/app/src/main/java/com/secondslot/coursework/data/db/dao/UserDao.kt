package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.secondslot.coursework.data.db.model.entity.UserEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY full_name")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE user_id = :userId")
    suspend fun getUser(userId: Int): UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
