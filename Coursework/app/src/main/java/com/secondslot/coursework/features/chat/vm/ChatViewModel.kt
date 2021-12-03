package com.secondslot.coursework.features.chat.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondslot.coursework.core.Event
import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.domain.model.Reaction
import com.secondslot.coursework.domain.usecase.*
import com.secondslot.coursework.extentions.getDateForChat
import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.model.DateDivider
import com.secondslot.coursework.features.chat.model.MessageItem
import com.secondslot.coursework.features.chat.model.MessageToItemMapper
import com.secondslot.coursework.features.chat.ui.ChooseReactionListener
import com.secondslot.coursework.features.chat.ui.MessageInteractionListener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getOwnProfileUseCase: GetOwnProfileUseCase,
    private val addReactionUseCase: AddReactionUseCase,
    private val removeReactionUseCase: RemoveReactionUseCase,
    private val getReactionsUseCase: GetReactionsUseCase,
    private val getStreamByIdUseCase: GetStreamByIdUseCase
) : ViewModel(), MessageInteractionListener, ChooseReactionListener {

    private var myId: Int = -1
    private var firstMessageId: Int = -1
    private var lastMessageId: Int = -1
    private var messages: List<ChatItem> = arrayListOf()
    var chosenMessage: MessageItem? = null

    var streamId: Int? = null
    var topicName: String? = null
    private var streamName: String? = null

    private var isLoading: Boolean = false
        set(value) {
            field = value
            Log.d(TAG, "isLoading = $field")
        }

    private val _updateMessagesFlow = MutableStateFlow(Event(Pair(listOf<ChatItem>(), false)))
    val updateMessagesFlow: StateFlow<Event<Pair<List<ChatItem>, Boolean>>> = _updateMessagesFlow

    private val _showErrorFlow = MutableStateFlow<Event<Throwable?>>(Event(null))
    val showErrorFlow: StateFlow<Event<Throwable?>> = _showErrorFlow

    private val _showSendMessageErrorFlow = MutableStateFlow<Event<Throwable?>>(Event(null))
    val showSendMessageErrorFlow: StateFlow<Event<Throwable?>> = _showSendMessageErrorFlow

    private val _clearMessageFlow = MutableStateFlow(Event(false))
    val clearMessageFlow: StateFlow<Event<Boolean>> = _clearMessageFlow

    private val _openReactionsSheet = MutableStateFlow(Event(false))
    val openReactionsSheet: StateFlow<Event<Boolean>> = _openReactionsSheet

    private val _closeReactionsSheet = MutableStateFlow(Event(false))
    val closeReactionsSheet: StateFlow<Event<Boolean>> = _closeReactionsSheet

    fun getMyId(): Flow<Int> {
        // Get own profile for using it to view initializing
        return if (myId == -1) {
            getOwnProfileUseCase.execute()
                .map { users ->
                    myId = users[0].userId
                    myId
                }
        } else {
            flowOf(myId)
        }
    }

    suspend fun getStreamName(): String {
        return streamName ?: getStreamByIdUseCase.execute(streamId!!).streamName
    }

    fun getMessages(
        anchor: String = "newest",
        isLoadNew: Boolean = false,
        isScrollToEnd: Boolean
    ) {
        if (isLoading) return
        isLoading = true

        val numBefore: String
        val numAfter: String

        if (isLoadNew) {
            numBefore = MESSAGES_IN_OPPOSITE_DIRECTION
            numAfter = MESSAGES_PER_PAGE
        } else {
            numBefore = MESSAGES_PER_PAGE
            numAfter = MESSAGES_IN_OPPOSITE_DIRECTION
        }

        viewModelScope.launch {

            getMessagesUseCase.execute(
                anchor = anchor,
                numBefore = numBefore,
                numAfter = numAfter,
                narrow = getNarrow()
            )
                .map { MessageToItemMapper.map(it) }
                .onCompletion { isLoading = false }
                .catch { _showErrorFlow.value = Event(it) }
                .collect {

                    Log.d(TAG, "isLoadNew = $isLoadNew")
                    if (it.isNotEmpty()) {
                        Log.d(TAG, "Received List<MessageItem> size = ${it.size}")

                        if (isLoadNew) {
                            // Remove last item, because it will be at the next batch
                            if (messages.isNotEmpty()) {
                                (messages as ArrayList).removeAt(messages.size - 1)
                            }
                            // Add new messages
                            (messages as ArrayList).addAll(it)
                        } else {
                            if (messages.isNotEmpty()) {
                                // Remove item at index 0 - time divider
                                // Remove item at index 1 - first message, because it will be at the
                                // next batch
                                (messages as ArrayList).subList(0, 2).clear()
                            }
                            // Add new messages at the start
                            (messages as ArrayList).addAll(0, it)
                            firstMessageId = (messages[0] as MessageItem).id
                        }
                        lastMessageId = (messages[messages.size - 1] as MessageItem).id

                        updateMessages(isScrollToEnd)
                    } else {
                        Log.d(TAG, "Messages flow is empty")
                    }
                }
        }
    }

    private fun getNarrow(): Map<String, Any> = mapOf(
        "stream" to (streamId ?: 0),
        "topic" to (topicName ?: "")
    )

    private fun updateMessages(isScrollToEnd: Boolean = false) {
        for (i in messages.size - 1 downTo 1) {
            if (messages[i - 1] is MessageItem && messages[i] is MessageItem) {
                if ((messages[i - 1] as MessageItem).timestamp.getDateForChat() !=
                    (messages[i] as MessageItem).timestamp.getDateForChat()
                ) {

                    (messages as ArrayList).add(
                        i, DateDivider(
                            (messages[i] as MessageItem)
                                .timestamp.getDateForChat()
                        )
                    )
                }
            }
        }

        if (messages[0] !is DateDivider) {
            (messages as ArrayList).add(
                0, DateDivider(
                    (messages[0] as MessageItem)
                        .timestamp.getDateForChat()
                )
            )
        }

        _updateMessagesFlow.value = Event(Pair(messages, isScrollToEnd))
    }

    fun onScrollUp(firstVisiblePosition: Int) {
        if (isLoading) return

        // If item on position to prefetch is visible
        if (firstVisiblePosition == PREFETCH_DISTANCE) {
            Log.d(TAG, "It's time to prefetch")
            Log.d(TAG, "FirstMessageId = $firstMessageId")
            getMessages(
                anchor = firstMessageId.toString(),
                isLoadNew = false,
                isScrollToEnd = false
            )
        }
    }

    fun onScrollDown(lastVisiblePosition: Int) {
        if (isLoading) return

        // If last item is visible
        if (lastVisiblePosition == messages.size - 1) {
            Log.d(TAG, "Load new batch of messages")
            getMessages(
                anchor = lastMessageId.toString(),
                isLoadNew = true,
                isScrollToEnd = false
            )
        }
    }

    fun onSendMessageClicked(messageText: String) {
        viewModelScope.launch {
            try {
                val sendMessageResult = sendMessageUseCase.execute(
                    streamId = streamId ?: 0,
                    topicName = topicName ?: "",
                    messageText = messageText
                )

                if (sendMessageResult.result == SERVER_RESULT_SUCCESS) {
                    _clearMessageFlow.value = Event(true)
                    getMessages(
                        anchor = lastMessageId.toString(),
                        isLoadNew = true,
                        isScrollToEnd = true
                    )
                } else {
                    _showSendMessageErrorFlow.value = Event(null)
                }
            } catch (e: Exception) {
                _showSendMessageErrorFlow.value = Event(e)
            }
        }
    }

    fun getReactions(): List<ReactionLocal> {
        return getReactionsUseCase.execute()
    }

    override fun openReactionsSheet(message: MessageItem) {
        chosenMessage = message
        _openReactionsSheet.value = Event(true)
    }

    override fun reactionChosen(reaction: ReactionLocal) {
        _closeReactionsSheet.value = Event(true)

        var existingReaction: Reaction? = null
        chosenMessage?.reactions?.forEach {
            if (it.key.emojiCode == reaction.emojiCode && it.key.userId == myId) {
                existingReaction = it.key
                return
            }
        }

        if (existingReaction == null) {
            chosenMessage?.id?.let { addReaction(it, reaction.emojiName) }
        } else {
            chosenMessage?.id?.let { removeReaction(it, reaction.emojiName) }
        }
    }

    override fun addReaction(messageId: Int, emojiName: String) {
        viewModelScope.launch {
            try {
                val sendResult = addReactionUseCase.execute(messageId, emojiName)

                if (sendResult.result == SERVER_RESULT_SUCCESS) {
                    Log.d(TAG, "Server result = ${sendResult.result}")
                    updateMessage(messageId)
                } else {
                    _showErrorFlow.value = Event(null)
                    Log.e(TAG, "Error adding reaction = ${sendResult.result}")
                }
            } catch (e: Exception) {
                _showErrorFlow.value = Event(e)
                Log.e(TAG, "Error adding reaction = ${e.message}")
            }
        }
    }

    override fun removeReaction(messageId: Int, emojiName: String) {
        viewModelScope.launch {
            try {
                val sendResult = removeReactionUseCase.execute(messageId, emojiName)

                if (sendResult.result == SERVER_RESULT_SUCCESS) {
                    updateMessage(messageId)
                } else {
                    _showErrorFlow.value = Event(null)
                    Log.e(TAG, "Error removing reaction = ${sendResult.result}")
                }

            } catch (e: Exception) {
                _showErrorFlow.value = Event(e)
                Log.e(TAG, "Error removing reaction = ${e.message}")
            }
        }
    }

    private fun updateMessage(messageId: Int) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {

            getMessagesUseCase.execute(
                anchor = messageId.toString(),
                narrow = getNarrow()
            )
                .map { MessageToItemMapper.map(it) }
                .onCompletion { isLoading = false }
                .catch { _showErrorFlow.value = Event(it) }
                .collect { updatedMessageList ->

                    if (updatedMessageList.isNotEmpty()) {
                        val messageToUpdate = messages.find {
                            (it is MessageItem) && it.id == updatedMessageList[0].id
                        }

                        val indexToUpdate = messages.indexOf(messageToUpdate)
                        if (indexToUpdate != -1) {
                            (messages as ArrayList)[indexToUpdate] = updatedMessageList[0]
                            updateMessages()
                        }
                    } else {
                        Log.d(TAG, "Messages flow is empty")
                    }
                }
        }
    }

    companion object {
        private const val TAG = "ChatViewModel"

        private const val PREFETCH_DISTANCE = 4
        private const val MESSAGES_PER_PAGE = "20"
        private const val MESSAGES_IN_OPPOSITE_DIRECTION = "0"

        private const val SERVER_RESULT_SUCCESS = "success"
    }
}
