package com.secondslot.coursework.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.secondslot.coursework.base.mapper.BaseMapper
import com.secondslot.coursework.data.db.model.entity.MessageEntity
import com.secondslot.coursework.data.db.model.entity.ReactionEntity
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.model.Reaction

class MessageReactionDb(
    @Embedded val messageEntity: MessageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "message_id"
    )
    val reactionEntities: List<ReactionEntity>
)

fun MessageReactionDb.toDomainModel(): Message = Message(
    id = this.messageEntity.id,
    senderId = this.messageEntity.senderId,
    senderFullName = this.messageEntity.senderFullName,
    avatarUrl = this.messageEntity.avatarUrl,
    content = this.messageEntity.content,
    topicName = this.messageEntity.topicName,
    timestamp = this.messageEntity.timestamp,
    isMeMessage = this.messageEntity.isMeMessage,
    reactions = this.reactionEntities.map { reactionEntity ->
        Reaction(
            emojiName = reactionEntity.emojiName,
            emojiCode = reactionEntity.emojiCode,
            reactionType = reactionEntity.reactionType,
            userId = reactionEntity.userId
        )
    }
)

object MessageReactionDbToDomainModel : BaseMapper<List<MessageReactionDb>, List<Message>> {

    override fun map(type: List<MessageReactionDb>?): List<Message> {
        return type?.map { it.toDomainModel() } ?: emptyList()
    }
}
