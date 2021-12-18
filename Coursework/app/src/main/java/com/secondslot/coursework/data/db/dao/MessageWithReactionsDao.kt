package com.secondslot.coursework.data.db.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.secondslot.coursework.data.db.model.MessageReactionDb
import com.secondslot.coursework.data.db.model.entity.MessageEntity
import com.secondslot.coursework.data.db.model.entity.ReactionEntity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

@Dao
abstract class MessageWithReactionsDao : MessageDao, ReactionDao {

    @Transaction
    @Query("SELECT * FROM messages WHERE topic_name = :topicName")
    abstract fun getMessagesReactions(topicName: String): Single<List<MessageReactionDb>>

    @Transaction
    @Query("SELECT * FROM messages WHERE id = :messageId")
    abstract fun getMessageReactions(messageId: Int): Single<MessageReactionDb>

    private fun insertMessagesReactions(
        messages: List<MessageEntity>, reactions: List<ReactionEntity>
    ): Completable {

        return Completable.concatArray(
            insertMessages(messages).subscribeOn(Schedulers.io())
                .doOnError { Log.d(TAG, "insertMessages error") }
                .doOnComplete { Log.d(TAG, "insertMessages complete") },
            insertReactions(reactions).subscribeOn(Schedulers.io())
        )
    }

    private fun deleteMessagesReactions(topicName: String): Completable {
        return deleteMessages(topicName)
        // Reactions will be deleted automatically because they have "message_id" as a foreign key
    }

    fun deleteMessageReactions(messageId: Int): Completable {
        return deleteMessage(messageId)
        // Reactions will be deleted automatically because they have "message_id" as a foreign key
    }

    fun editMessageReactions(messageId: Int, newMessageText: String): Completable {
        return editMessage(messageId, newMessageText)
    }

    fun moveMessageReactions(messageId: Int, newTopic: String): Completable {
        return moveMessage(messageId, newTopic)
    }

    fun updateMessagesReactions(
        messages: List<MessageEntity>,
        reactions: List<ReactionEntity>,
        topicName: String
    ): Completable {

        return Completable.concatArray(
            deleteMessagesReactions(topicName).subscribeOn(Schedulers.io())
                .doOnError { Log.d(TAG, "updateMessages error") }
                .doOnComplete { Log.d(TAG, "updateMessages complete") },
            insertMessagesReactions(messages, reactions).subscribeOn(Schedulers.io())
        )
    }

    companion object {
        private const val TAG = "MessageReactionDao"
    }
}
