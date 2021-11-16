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
    fun getAllUsers(): Single<List<UserEntity>>

    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun getUser(userId: Int): Single<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: List<UserEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity): Completable

    @Query("DELETE FROM users")
    fun deleteAllUsers(): Completable
}
