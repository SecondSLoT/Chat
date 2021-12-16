package com.secondslot.coursework.features.chat.ui

import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.model.MessageMenuItem

interface ChatView {

    fun initViews(myId: Int)

    fun showStreamName(streamName: String)

    fun openMessageMenu(menuList: List<MessageMenuItem>)

    fun closeMessageMenu()

    fun openReactionsSheet()

    fun closeReactionsSheet()

    fun updateMessages(messages: List<ChatItem>, isScrollToEnd: Boolean = false)

    fun showSendMessageError(error: Throwable? = null)

    fun showError(error: Throwable? = null)

    fun clearMessageEditText()

    fun openEditMessageDialog(curMessageText: String)

    fun openDeleteMessageDialog()
}


