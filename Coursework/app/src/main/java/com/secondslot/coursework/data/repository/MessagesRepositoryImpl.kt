package com.secondslot.coursework.data.repository

import android.util.Log
import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.MessageRemoteToMessageMapper
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.data.db.model.MessageReactionDbToDomainModel
import com.secondslot.coursework.data.db.model.entity.MessageEntity
import com.secondslot.coursework.data.db.model.entity.ReactionEntity
import com.secondslot.coursework.data.db.model.entity.ReactionToReactionEntityMapper
import com.secondslot.coursework.data.api.model.SendResult
import com.secondslot.coursework.data.api.model.response.toSendResult
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MessagesRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val networkManager: NetworkManager
) : MessagesRepository {

    private var messagesCache = emptyList<Message>()

    override fun getMessages(
        anchor: String,
        numBefore: String,
        numAfter: String,
        narrow: Map<String, Any>
    ): Observable<List<Message>> {

        val topicName = narrow["topic"] as String
        var messagesReactionsDbObservable: Observable<List<Message>> = Observable.just(emptyList())

        if (anchor == "newest" || anchor == "first_unread") {

            // Data from DB
            messagesReactionsDbObservable = database.messageWithReactionDao
                .getMessagesReactions(topicName)
                .map { messageReactionDbList ->
                    Log.d(TAG, "MessagesReactionsDb size = ${messageReactionDbList.size}")
                    messagesCache = MessageReactionDbToDomainModel.map(messageReactionDbList)
                    messagesCache
                }
                .toObservable()
        }

        // Data from network
        val messagesReactionsRemoteObservable = networkManager
            .getMessages(anchor, numBefore, numAfter, narrow)
            .map { messageRemoteList ->
                MessageRemoteToMessageMapper.map(messageRemoteList)
            }
            // Save data from network to DB
            .flatMap { messages ->
                messagesCache = mergeData(messagesCache, messages)

                val messageEntities = messagesCache.map { message ->
                    MessageEntity.fromMessage(message)
                }
                val reactionEntities = messagesCache.flatMap { message ->
                    ReactionToReactionEntityMapper.map(message.reactions, message.id)
                }

                database.messageWithReactionDao
                    .updateMessagesReactions(messageEntities, reactionEntities, topicName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnComplete { Log.d(TAG, "updateMessagesReactions complete") }
                    .andThen(Observable.just(messages))
            }

        return Observable
            .concat(
                messagesReactionsDbObservable,
                messagesReactionsRemoteObservable
            )
            .filter { messages -> messages.isNotEmpty() }
            .first(emptyList())
            .toObservable()
    }

    private fun mergeData(oldData: List<Message>, newData: List<Message>): List<Message> {

        val newDataIds = newData.map { it.id }

        // Delete intersections with newData from currentData
        val result = oldData.filterNot { it.id in newDataIds }
        // Add newData
        (result as ArrayList).addAll(newData)
        // Sort by id (or by timestamp?)
        result.sortBy { it.id }
        // Delete old items if size is greater than MAX_ITEMS_IN_DB
        if (result.size > MAX_ITEMS_IN_DB)
            result.subList(0, result.size - MAX_ITEMS_IN_DB).clear()
        return result
    }

    override fun sendMessage(
        type: String,
        streamId: Int,
        topicName: String,
        messageText: String
    ): Single<SendResult> {
        return networkManager.sendMessage(type, streamId, topicName, messageText)
            .map { it.toSendResult() }
    }

    companion object {
        private const val MAX_ITEMS_IN_DB = 50
        private const val TAG = "MessagesRepositoryImpl"
    }
}
