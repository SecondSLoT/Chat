package com.secondslot.coursework.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.secondslot.coursework.data.db.model.entity.MessageEntity
import com.secondslot.coursework.data.db.model.entity.ReactionEntity

class MessageReaction(
    @Embedded val messageEntity: MessageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "message_d"
    )
    val reactionEntity: ReactionEntity
)
