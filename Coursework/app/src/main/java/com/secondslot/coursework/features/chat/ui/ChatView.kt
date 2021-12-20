package com.secondslot.coursework.features.chat.ui

import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.model.MessageMenuItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface ChatView : MvpView {

    @AddToEndSingle
    fun initViews(myId: Int)

    @AddToEndSingle
    fun showStreamName(streamName: String)

    @AddToEndSingle
    fun switchMessageMenu(show: Boolean, menuList: List<MessageMenuItem> = emptyList())

    @AddToEndSingle
    fun switchReactionsSheet(show: Boolean)

    @AddToEndSingle
    fun updateMessages(messages: List<ChatItem>, isScrollToEnd: Boolean = false)

    @OneExecution
    fun showSendMessageError(error: Throwable? = null)

    @OneExecution
    fun showError(error: Throwable? = null)

    @AddToEndSingle
    fun switchRetryButton(show: Boolean)

    @OneExecution
    fun notifyMessageMoved(topicName: String)

    @OneExecution
    fun notifySameTopic()

    @OneExecution
    fun notifyCopiedToClipboard()

    @OneExecution
    fun clearMessageEditText()

    @OneExecution
    fun openEditMessageDialog(show: Boolean, curMessageText: String = "")

    @OneExecution
    fun openDeleteMessageDialog()

    @OneExecution
    fun openMoveMessageDialog(topics: List<String>)
}
