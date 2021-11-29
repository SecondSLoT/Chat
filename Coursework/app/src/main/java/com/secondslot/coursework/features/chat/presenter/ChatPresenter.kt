package com.secondslot.coursework.features.chat.presenter

import android.util.Log
import com.secondslot.coursework.base.mvp.presenter.RxPresenter
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChatPresenter @Inject constructor(
    private val getStreamByIdUseCase: GetStreamByIdUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getOwnProfileUseCase: GetOwnProfileUseCase,
    private val addReactionUseCase: AddReactionUseCase,
    private val removeReactionUseCase: RemoveReactionUseCase,
    private val getReactionsUseCase: GetReactionsUseCase
) :
    RxPresenter<ChatContract.ChatView>(ChatContract.ChatView::class.java),
    ChatContract.ChatPresenter,
    MessageInteractionListener,
    ChooseReactionListener {

    private var myId: Int = -1
    private var firstMessageId: Int = -1
    private var lastMessageId: Int = -1
    private var messages: List<ChatItem> = arrayListOf()
    private var chosenMessage: MessageItem? = null

    private var streamId: Int? = null
    private var topicName: String? = null
    private var streamName: String? = null

    private var isLoading: Boolean = false
        set(value) {
            field = value
            Log.d(TAG, "isLoading = $field")
        }

    override fun getTopicName(): String = topicName ?: ""

    override fun getStreamName(): String = streamName ?: ""

    override fun getChosenMessage(): MessageItem? = chosenMessage

    override fun attachView(view: ChatContract.ChatView) {
        super.attachView(view)
        Log.d(TAG, "attachView()")

        streamId = view.getStreamId()
        topicName = view.getTopicName()
        streamId?.let {
            getStreamByIdUseCase.execute(streamId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = { streamName = it.streamName },
                    onError = {
                        Log.d(TAG, "getStreamByIdUseCase.execute() error")
                        view.showError(it)
                    }
                )
        }

        // Get own profile for using it to view initializing
        getOwnProfileUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { user ->
                    if (user.isNotEmpty()) {
                        myId = user[0].userId
                        view.initViews(myId)
                    }
                },
                onError = {
                    Log.d(TAG, "getOwnProfileUseCase.execute() error")
                    view.showError(it)
                }
            )
            .disposeOnFinish()
    }

    override fun detachView(isFinishing: Boolean) {
        super.detachView(isFinishing)
        Log.d(TAG, "detachView()")
    }

    override fun getMessages(
        anchor: String,
        isLoadNew: Boolean,
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

        getMessagesUseCase.execute(
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
                onError = { view?.showError(it) },
            )
            .disposeOnFinish()
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

        view?.updateMessages(messages, isScrollToEnd)
    }

    override fun onScrollUp(firstVisiblePosition: Int) {
        if (isLoading) return

        // If item on position to prefetch is visible
        if (firstVisiblePosition == PREFETCH_DISTANCE) {
            Log.d(TAG, "It's time to prefetch")
            getMessages(
                anchor = firstMessageId.toString(),
                isLoadNew = false,
                isScrollToEnd = false
            )
        }
    }

    override fun onScrollDown(lastVisiblePosition: Int) {
        if (isLoading) return

        // If last item is visible
        if (lastVisiblePosition == messages.size - 1) {
            Log.d(TAG, "Load new banch of messages")
            getMessages(anchor = lastMessageId.toString(), isLoadNew = true, isScrollToEnd = false)
        }
    }

    override fun onSendMessageClicked(messageText: String) {
        val sendMessageResultSingle = sendMessageUseCase.execute(
            streamId = streamId ?: 0,
            topicName = topicName ?: "",
            messageText = messageText
        )
        sendMessageResultSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    if (it.result == SERVER_RESULT_SUCCESS) {
                        view?.clearMessageEditText()
                        getMessages(
                            anchor = lastMessageId.toString(),
                            isLoadNew = true,
                            isScrollToEnd = true
                        )
                    } else {
                        view?.showSendMessageError()
                    }
                },
                onError = { view?.showSendMessageError(it) }
            )
            .disposeOnFinish()
    }

    override fun getReactions(): List<ReactionLocal> {
        return getReactionsUseCase.execute()
    }

    override fun openReactionsSheet(message: MessageItem) {
        chosenMessage = message
        view?.openReactionsSheet()
    }

    override fun reactionChosen(reaction: ReactionLocal) {
        view?.closeReactionsSheet()

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

        addReactionUseCase.execute(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    if (it.result == SERVER_RESULT_SUCCESS) {
                        updateMessage(messageId)
                    } else {
                        view?.showError()
                        Log.e(TAG, "Error adding reaction = ${it.result}")
                    }
                },
                onError = {
                    view?.showError()
                    Log.e(TAG, "Error adding reaction = ${it.message}")
                }
            )
            .disposeOnFinish()
    }

    override fun removeReaction(messageId: Int, emojiName: String) {

        removeReactionUseCase.execute(messageId, emojiName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    if (it.result == SERVER_RESULT_SUCCESS) {
                        updateMessage(messageId)
                    } else {
                        view?.showError()
                        Log.e(TAG, "Error removing reaction = ${it.result}")
                    }
                },
                onError = {
                    view?.showError()
                    Log.e(TAG, "Error removing reaction = ${it.message}")
                }
            )
            .disposeOnFinish()
    }

    private fun updateMessage(messageId: Int) {
        if (isLoading) return
        isLoading = true

        getMessagesUseCase.execute(
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
                onError = { view?.showError(it) },
            )
            .disposeOnFinish()
    }

    companion object {
        private const val TAG = "ChatPresenter"

        private const val PREFETCH_DISTANCE = 4
        private const val MESSAGES_PER_PAGE = "20"
        private const val MESSAGES_IN_OPPOSITE_DIRECTION = "0"

        private const val SERVER_RESULT_SUCCESS = "success"
    }
}
