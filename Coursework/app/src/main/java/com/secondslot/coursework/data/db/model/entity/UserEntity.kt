package com.secondslot.coursework.data.db.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.secondslot.coursework.core.mapper.BaseMapper
import com.secondslot.coursework.domain.model.User

@Entity(tableName = "users")
class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "date_joined") val dateJoined: String?,
) {

    companion object {

        fun fromDomainModel(user: User): UserEntity = UserEntity(
            userId = user.userId,
            fullName = user.fullName,
            avatarUrl = user.avatarUrl,
            email = user.email,
            dateJoined = user.dateJoined
        )
    }
}

fun UserEntity.toDomainModel(): User = User(
    userId = this.userId,
    fullName = this.fullName,
    avatarUrl = this.avatarUrl,
    email = this.email,
    dateJoined = this.dateJoined
)

object UserEntityToUserMapper : BaseMapper<List<UserEntity>, List<User>> {

    override fun map(type: List<UserEntity>?): List<User> {
        return type?.map {
            it.toDomainModel()
        } ?: emptyList()
    }
}

object UserToUserEntityMapper : BaseMapper<List<User>, List<UserEntity>> {

    override fun map(type: List<User>?): List<UserEntity> {
        return type?.map {
            UserEntity(
                userId = it.userId,
                fullName = it.fullName,
                avatarUrl = it.avatarUrl,
                email = it.email,
                dateJoined = it.dateJoined
            )
        } ?: emptyList()
    }
}
