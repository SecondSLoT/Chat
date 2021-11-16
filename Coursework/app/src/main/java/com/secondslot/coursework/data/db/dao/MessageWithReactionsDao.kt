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
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

@Dao
abstract class MessageWithReactionsDao : MessageDao, ReactionDao {

    @Transaction
    @Query("SELECT * FROM messages WHERE topic_name = :topicName")
    abstract fun getMessagesReactions(topicName: String): Single<List<MessageReactionDb>>

    fun insertMessagesReactions(messages: List<MessageEntity>, reactions: List<ReactionEntity>) {
        val messagesCompletable = insertMessages(messages)
        val reactionsCompletable = insertReactions(reactions)

        val messagesDisposable = messagesCompletable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onComplete = { Log.d(TAG, "insertMessages complete") },
                onError = { Log.d(TAG, "insertMessages error") }
            )

        val reactionDisposable = reactionsCompletable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onComplete = { Log.d(TAG, "insertReactions complete") },
                onError = { Log.d(TAG, "insertReactions error") }
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
    ) {
        val deleteCompletable = deleteMessagesReactions(topicName)
//        val insertCompletable = insertMessagesReactions(messages, reactions)

        val disposable = deleteCompletable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onComplete = { insertMessagesReactions(messages, reactions)}
            )
    }

    companion object {
        private const val TAG = "MessageReactionDao"
    }
}
