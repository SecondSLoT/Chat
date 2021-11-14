package com.secondslot.coursework.data.db.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "avatar_url")  val avatarUrl: String?,
    @ColumnInfo(name = "email")  val email: String?,
)
