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

object MessageReactionDbToDomainModel : BaseMapper<List<MessageReactionDb>, List<Message>> {

    override fun map(type: List<MessageReactionDb>?): List<Message> {
        return type?.map {
            Message(
                id = it.messageEntity.id,
                senderId = it.messageEntity.senderId,
                senderFullName = it.messageEntity.senderFullName,
                avatarUrl = it.messageEntity.avatarUrl,
                content = it.messageEntity.content,
                topicName = it.messageEntity.topicName,
                timestamp = it.messageEntity.timestamp,
                isMeMessage = it.messageEntity.isMeMessage,
                reactions = it.reactionEntities.map { reactionEntity ->
                    Reaction(
                        emojiName = reactionEntity.emojiName,
                        emojiCode = reactionEntity.emojiCode,
                        reactionType = reactionEntity.reactionType,
                        userId = reactionEntity.userId
                    )
                }
            )
        } ?: emptyList()
    }
}
