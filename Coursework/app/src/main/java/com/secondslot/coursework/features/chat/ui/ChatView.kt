package com.secondslot.coursework.features.chat.ui

import com.secondslot.coursework.features.chat.model.ChatItem

interface ChatView {

    fun initViews(myId: Int)

    fun showStreamName(streamName: String)

    fun openReactionsSheet()

    fun updateMessages(messages: List<ChatItem>, isScrollToEnd: Boolean = false)

    fun showSendMessageError(error: Throwable? = null)

    fun showError(error: Throwable? = null)

    fun clearMessageEditText()

    fun closeReactionsSheet()
}


