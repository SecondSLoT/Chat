package com.secondslot.coursework.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.secondslot.coursework.data.db.model.MessageReactionDb
import com.secondslot.coursework.data.db.model.entity.MessageEntity
import com.secondslot.coursework.data.db.model.entity.ReactionEntity

@Dao
abstract class MessageWithReactionsDao : MessageDao, ReactionDao {

    @Transaction
    @Query("SELECT * FROM messages WHERE topic_name = :topicName")
    abstract suspend fun getMessagesReactions(topicName: String): List<MessageReactionDb>

    private suspend fun insertMessagesReactions(
        messages: List<MessageEntity>,
        reactions: List<ReactionEntity>
    ) {
        insertMessages(messages)
        insertReactions(reactions)
    }

    private suspend fun deleteMessagesReactions(topicName: String) {
        deleteMessages(topicName)
        // Reactions will be deleted automatically because they have "message_id" as a foreign key
    }

    suspend fun updateMessagesReactions(
        messages: List<MessageEntity>,
        reactions: List<ReactionEntity>,
        topicName: String
    ) {
        deleteMessagesReactions(topicName)
        insertMessagesReactions(messages, reactions)
    }

    companion object {
        private const val TAG = "MessageReactionDao"
    }
}
