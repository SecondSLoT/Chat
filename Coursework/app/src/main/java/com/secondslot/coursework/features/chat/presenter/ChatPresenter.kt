package com.secondslot.coursework.features.chat.presenter

import android.app.Activity
import android.util.Log
import com.secondslot.coursework.R
import com.secondslot.coursework.base.mvp.MoxyRxPresenter
import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.domain.interactor.MessageInteractor
import com.secondslot.coursework.domain.interactor.ReactionInteractor
import com.secondslot.coursework.domain.interactor.StreamInteractor
import com.secondslot.coursework.domain.model.Reaction
import com.secondslot.coursework.domain.usecase.user.GetOwnProfileUseCase
import com.secondslot.coursework.extentions.fromHtml
import com.secondslot.coursework.extentions.getDateForChat
import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.model.DateDivider
import com.secondslot.coursework.features.chat.model.MessageItem
import com.secondslot.coursework.features.chat.model.MessageMenuItem
import com.secondslot.coursework.features.chat.model.MessageToItemMapper
import com.secondslot.coursework.features.chat.ui.ChatView
import com.secondslot.coursework.other.MyClipboardManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState

@InjectViewState
class ChatPresenter @AssistedInject constructor(
    private val messageInteractor: MessageInteractor,
    private val streamInteractor: StreamInteractor,
    private val getOwnProfileUseCase: GetOwnProfileUseCase,
    private val reactionInteractor: ReactionInteractor,
    private val myClipboardManager: MyClipboardManager,
    @Assisted private val streamId: Int,
    @Assisted private var topicName: String
) : MoxyRxPresenter<ChatView>() {

    private var myId: Int = -1
    private var firstMessageId: Int = -1
    private var lastMessageId: Int = -1
    private var messages: List<ChatItem> = arrayListOf()
    private var chosenMessage: MessageItem? = null

    private val menuList = listOf(
        MessageMenuItem(
            0,
            R.string.add_reaction,
            R.drawable.ic_baseline_mood_24,
            false
        ),
        MessageMenuItem(
            1,
            R.string.edit_message,
            R.drawable.ic_baseline_edit_24,
            true
        ),
        MessageMenuItem(
            2,
            R.string.delete_message,
            R.drawable.ic_baseline_delete_24,
            true
        ),
        MessageMenuItem(
            3,
            R.string.move_to_topic,
            R.drawable.ic_baseline_multiple_stop_24,
            true
        ),
        MessageMenuItem(
            4,
            R.string.copy_to_clipboard,
            R.drawable.ic_baseline_content_copy_24,
            false
        )
    )

    private var isLoading: Boolean = false
        set(value) {
            field = value
            Log.d(TAG, "isLoading = $field")
        }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG, "attachView()")
        // Get own profile for using it to view initializing
        getOwnProfile()
        getStreamById()
    }

    private fun getOwnProfile() {
        getOwnProfileUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { user ->
                    if (user.isNotEmpty() && myId == -1) {
                        myId = user[0].userId
                        viewState.initViews(myId)
                    }
                },
                onError = {
                    Log.d(TAG, "getOwnProfileUseCase.execute() error")
                    viewState.showError(it)
                    viewState.switchRetryButton(true)
                }
            )
            .disposeOnFinish()
    }

    private fun getStreamById() {
        streamInteractor.getStreamById(streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { viewState.showStreamName(it.streamName) },
                onError = {
                    Log.d(TAG, "getStreamByIdUseCase.execute() error")
                    viewState.showError(it)
                    viewState.switchRetryButton(true)
                }
            )
            .disposeOnFinish()
    }

    override fun detachView(view: ChatView?) {
        super.detachView(view)
        Log.d(TAG, "detachView()")
    }

    fun onViewsInitialized(isInRestoreState: Boolean) {
        Log.d(TAG, "isInRestoreState = $isInRestoreState")
        if (isInRestoreState) {
            viewState.updateMessages(messages)
        } else {
            loadMessages(
                anchor = "newest",
                isLoadNew = false,
                isScrollToEnd = true
            )
        }
    }

    fun loadMessages(
        anchor: String,
        isLoadNew: Boolean,
        isScrollToEnd: Boolean
    ) {
        Log.d(TAG, "loadMessages() called")
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

        messageInteractor.getMessages(
            anchor = anchor,
            numBefore = numBefore,
            numAfter = numAfter,
            narrow = getNarrow()
        )
            .subscribeOn(Schedulers.io())
            .map { MessageToItemMapper.map(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate { isLoading = false }
            .subscribeBy(
                onNext = {
                    Log.d(TAG, "MessageObservable onNext")
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
                        Log.d(TAG, "MessageObservable is empty")
                    }
                },
                onError = {
                    viewState.showError(it)
                    viewState.switchRetryButton(true)
                }
            )
            .disposeOnFinish()
    }

    private fun getNarrow(): Map<String, Any> = mapOf(
        "stream" to streamId,
        "topic" to topicName
    )

    private fun updateMessages(isScrollToEnd: Boolean = false) {
        if (messages.last() is DateDivider) (messages as ArrayList).remove(messages.last())

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

        if (messages.isNotEmpty() && messages[0] !is DateDivider) {
            (messages as ArrayList).add(
                0, DateDivider(
                    (messages[0] as MessageItem)
                        .timestamp.getDateForChat()
                )
            )
        }

        Log.d(TAG, "messages.size = ${messages.size}")
        viewState.updateMessages(messages, isScrollToEnd)
    }

    fun onScrollUp(firstVisiblePosition: Int) {
        if (isLoading) return

        // If item on position to prefetch is visible
        if (firstVisiblePosition == PREFETCH_DISTANCE) {
            Log.d(TAG, "It's time to prefetch")
            loadMessages(
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
            Log.d(TAG, "Load new banch of messages")
            loadMessages(anchor = lastMessageId.toString(), isLoadNew = true, isScrollToEnd = false)
        }
    }

    fun onSendMessageClicked(messageText: String) {
        val sendMessageResultSingle = messageInteractor.sendMessage(
            streamId = streamId,
            topicName = topicName,
            messageText = messageText
        )
        sendMessageResultSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    if (it.result == SERVER_RESULT_SUCCESS) {
                        viewState.clearMessageEditText()
                        loadMessages(
                            anchor = lastMessageId.toString(),
                            isLoadNew = true,
                            isScrollToEnd = true
                        )
                    } else {
                        viewState.showSendMessageError()
                    }
                },
                onError = { viewState.showSendMessageError(it) }
            )
            .disposeOnFinish()
    }

    fun getReactions(): List<ReactionLocal> {
        return reactionInteractor.getReactions()
    }

    fun onMessageLongClick(message: MessageItem) {
        chosenMessage = message
        if (message.senderId == myId) {
            viewState.switchMessageMenu(true, menuList)
        } else {
            viewState.switchMessageMenu(true, menuList.filter { !it.onlyForMessageAuthor })
        }
    }

    fun onAddReactionButtonClicked(message: MessageItem) {
        chosenMessage = message
        viewState.switchReactionsSheet(true)
    }

    fun onReactionChosen(reaction: ReactionLocal) {
        viewState.switchMessageMenu(false)
        viewState.switchReactionsSheet(false)

        var existingReaction: Reaction? = null
        chosenMessage?.reactions?.forEach { reactionMapEntry ->
            if (myId != -1 &&
                reactionMapEntry.key == reaction.emojiCode &&
                reactionMapEntry.value.find { it.userId == myId } != null
            ) {
                existingReaction = reactionMapEntry.value[0]
                return
            }
        }

        if (existingReaction == null) {
            chosenMessage?.id?.let { onAddReaction(it, reaction.emojiName) }
        } else {
            chosenMessage?.id?.let { onRemoveReaction(it, reaction.emojiName) }
        }
    }

    fun onAddReaction(messageId: Int, emojiName: String) {

        reactionInteractor.addReaction(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    if (it.result == SERVER_RESULT_SUCCESS) {
                        updateMessage(messageId)
                    } else {
                        viewState.showError()
                        Log.e(TAG, "Error adding reaction = ${it.result}")
                    }
                },
                onError = {
                    viewState.showError()
                    Log.e(TAG, "Error adding reaction = ${it.message}")
                }
            )
            .disposeOnFinish()
    }

    fun onRemoveReaction(messageId: Int, emojiName: String) {

        reactionInteractor.removeReaction(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    if (it.result == SERVER_RESULT_SUCCESS) {
                        updateMessage(messageId)
                    } else {
                        viewState.showError()
                        Log.e(TAG, "Error removing reaction = ${it.result}")
                    }
                },
                onError = {
                    viewState.showError()
                    Log.e(TAG, "Error removing reaction = ${it.message}")
                }
            )
            .disposeOnFinish()
    }

    private fun updateMessage(messageId: Int) {
        if (isLoading) return
        isLoading = true

        messageInteractor.getMessages(
            anchor = messageId.toString(),
            narrow = getNarrow()
        )
            .subscribeOn(Schedulers.io())
            .map { MessageToItemMapper.map(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate { isLoading = false }
            .subscribeBy(
                onNext = { updatedMessageList ->
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
                        Log.d(TAG, "MessageObservable is empty")
                    }
                },
                onError = {
                    viewState.showError(it)
                    viewState.switchRetryButton(true)
                }
            )
            .disposeOnFinish()
    }

    fun onRetryClicked() {
        viewState.switchRetryButton(false)
        loadMessages(
            anchor = "newest",
            isLoadNew = false,
            isScrollToEnd = true
        )
    }

    fun onMessageMenuItemClick(itemId: Int) {
        when (itemId) {
            0 -> viewState.switchReactionsSheet(true)
            1 -> {
                viewState.openEditMessageDialog(
                    true,
                    chosenMessage?.content ?: ""
                )
            }
            2 -> viewState.openDeleteMessageDialog()
            3 -> {
                streamInteractor.getTopics(streamId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onNext = { topics ->
                            viewState.openMoveMessageDialog(topics.map { it.topicName })
                        },
                        onError = { viewState.showError(it) }
                    )
                    .disposeOnFinish()
            }
            4 -> {
                myClipboardManager.copyToClipboard(chosenMessage?.content?.fromHtml() ?: "")
                viewState.run {
                    switchMessageMenu(false)
                    notifyCopiedToClipboard()
                }
            }
        }
    }

    fun onEditMessage(result: Int, newMessageText: String) {
        viewState.switchMessageMenu(false)

        if (result == Activity.RESULT_OK) {
            chosenMessage?.id?.let { messageId ->
                messageInteractor.editMessage(messageId, newMessageText)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onSuccess = {
                            Log.d(TAG, "Edit message: ${it.result}")
                            updateMessage(messageId)
                        },
                        onError = { Log.d(TAG, "Error edit message: ${it.message}") }
                    )
            }
        }
    }

    fun onDeleteMessage(result: Int) {
        viewState.switchMessageMenu(false)

        if (result == Activity.RESULT_OK) {
            chosenMessage?.id?.let { messageId ->
                messageInteractor.deleteMessage(messageId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onSuccess = {
                            Log.d(TAG, "Delete message: ${it.result}")
                            (messages as ArrayList).remove(chosenMessage as ChatItem)
                            updateMessages()
                        },
                        onError = { Log.d(TAG, "Error: ${it.message}") }
                    )
            }
        }
    }

    fun onMoveMessage(result: Int, newTopic: String) {
        viewState.switchMessageMenu(false)

        if (result == Activity.RESULT_OK) {
            if (topicName != newTopic) {
                chosenMessage?.id?.let { messageId ->
                    messageInteractor.moveMessage(messageId, newTopic)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                            onSuccess = {
                                Log.d(TAG, "Move message: ${it.result}")
                                (messages as ArrayList).remove(chosenMessage as ChatItem)
                                updateMessages()
                                viewState.notifyMessageMoved(newTopic)
                            },
                            onError = { Log.d(TAG, "Error: ${it.message}") }
                        )
                }
            } else {
                viewState.notifySameTopic()
            }
        }
    }

    fun onDismissBottomSheetMessageMenu() {
        viewState.switchMessageMenu(false)
    }

    fun onDismissBottomSheetReactions() {
        viewState.switchReactionsSheet(false)
    }

    companion object {
        private const val TAG = "ChatPresenter"

        private const val PREFETCH_DISTANCE = 4
        private const val MESSAGES_PER_PAGE = "20"
        private const val MESSAGES_IN_OPPOSITE_DIRECTION = "0"

        private const val SERVER_RESULT_SUCCESS = "success"
    }
}
