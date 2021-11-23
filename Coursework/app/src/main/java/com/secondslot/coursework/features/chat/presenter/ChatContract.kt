package com.secondslot.coursework.features.chat.presenter

import com.secondslot.coursework.base.mvp.presenter.Presenter
import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.model.MessageItem

interface ChatContract {

    interface ChatView {

        fun getStreamId(): Int

        fun getTopicName(): String

        fun initViews(myId: Int)

        fun updateMessages(messages: List<ChatItem>, isScrollToEnd: Boolean = false)

        fun showSendMessageError(error: Throwable? = null)

        fun showError(error: Throwable? = null)

        fun clearMessageEditText()

        fun openReactionsSheet()

        fun closeReactionsSheet()
    }

    interface ChatPresenter : Presenter<ChatView> {

        fun getTopicName(): String

        fun getChosenMessage(): MessageItem?

        fun getMessages(
            anchor: String = "newest",
            isLoadNew: Boolean = false,
            isScrollToEnd: Boolean = false
        )

        fun onScrollUp(firstVisiblePosition: Int)

        fun onScrollDown(lastVisiblePosition: Int)

        fun onSendMessageClicked(messageText: String)

        fun getReactions(): List<ReactionLocal>
    }
}
