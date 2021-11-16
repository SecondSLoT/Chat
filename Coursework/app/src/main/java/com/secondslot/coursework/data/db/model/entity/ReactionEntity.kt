package com.secondslot.coursework.data.db.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import com.secondslot.coursework.domain.model.Reaction

@Entity(
    tableName = "reactions",
    primaryKeys = ["emoji_code", "user_id", "message_id"],
    foreignKeys = [ForeignKey(
        entity = MessageEntity::class,
        parentColumns = ["id"],
        childColumns = ["message_id"],
        onDelete = CASCADE
    )]
)
class ReactionEntity(
    @ColumnInfo(name = "emoji_name") val emojiName: String,
    @ColumnInfo(name = "emoji_code") val emojiCode: String,
    @ColumnInfo(name = "reaction_type") val reactionType: String,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "message_id") val messageId: Int // Foreign key
)

object ReactionToReactionEntityMapper {

    fun map(type: List<Reaction>?, messageId: Int): List<ReactionEntity> {
        return type?.map {
            ReactionEntity(
                emojiName = it.emojiName,
                emojiCode = it.emojiCode,
                reactionType = it.reactionType,
                userId = it.userId,
                messageId = messageId
            )
        } ?: emptyList()
    }
}
