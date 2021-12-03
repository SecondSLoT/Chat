package com.secondslot.coursework.data.repository

import android.util.Log
import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.model.SendResult
import com.secondslot.coursework.data.api.model.response.toSendResult
import com.secondslot.coursework.data.api.model.toDomainModel
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.data.db.model.entity.MessageEntity
import com.secondslot.coursework.data.db.model.entity.ReactionEntity
import com.secondslot.coursework.data.db.model.entity.ReactionToReactionEntityMapper
import com.secondslot.coursework.data.db.model.toDomainModel
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    ): Flow<List<Message>> = flow {

        val topicName = narrow["topic"] as String

        if (anchor == "newest" || anchor == "first_unread") {

            // Data from DB
            val messagesReactionsFromDb = database.messageWithReactionDao
                .getMessagesReactions(topicName)
                .map { it.toDomainModel() }

            Log.d(TAG, "MessagesReactionsDb size = ${messagesReactionsFromDb.size}")

            messagesCache = messagesReactionsFromDb
            if (messagesReactionsFromDb.isNotEmpty()) {
                emit(messagesReactionsFromDb)
                return@flow
            }
        }

        // Data from network
        val messagesReactionsFromNetwork = networkManager
            .getMessages(anchor, numBefore, numAfter, narrow)
            .map { it.toDomainModel() }
        if (messagesReactionsFromNetwork.isNotEmpty()) emit(messagesReactionsFromNetwork)

        // Save data to DB
        messagesCache = mergeData(messagesCache, messagesReactionsFromNetwork)

        val messageEntities: ArrayList<MessageEntity> = arrayListOf()
        val reactionEntities: ArrayList<ReactionEntity> = arrayListOf()

        messagesCache.forEach { message ->
            messageEntities.add(MessageEntity.fromMessage(message))
            reactionEntities.addAll(
                ReactionToReactionEntityMapper.map(message.reactions, message.id)
            )
        }

        database.messageWithReactionDao
            .updateMessagesReactions(messageEntities, reactionEntities, topicName)
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

    override suspend fun sendMessage(
        type: String,
        streamId: Int,
        topicName: String,
        messageText: String
    ): SendResult {
        return networkManager.sendMessage(type, streamId, topicName, messageText).toSendResult()
    }

    companion object {
        private const val MAX_ITEMS_IN_DB = 50
        private const val TAG = "MessagesRepositoryImpl"
    }
}
