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
