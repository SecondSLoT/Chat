package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.secondslot.coursework.data.db.model.entity.UserEntity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

@Dao
abstract class UserDao {

    @Query("SELECT * FROM users ORDER BY full_name")
    abstract fun getAllUsers(): Single<List<UserEntity>>

    @Query("SELECT * FROM users WHERE user_id = :userId")
    abstract fun getUser(userId: Int): Single<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUsers(users: List<UserEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUser(user: UserEntity): Completable

    @Query("DELETE FROM users")
    abstract fun deleteAllUsers(): Completable

    fun updateUsers(users: List<UserEntity>): Completable {
        return deleteAllUsers().subscribeOn(Schedulers.io())
            .andThen(insertUsers(users).subscribeOn(Schedulers.io()))
    }
}
