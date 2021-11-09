package com.secondslot.coursework.features.chat.ui

import com.secondslot.coursework.features.chat.model.MessageItem

interface MessageInteractionListener {

    fun openReactionsSheet(message: MessageItem)

    fun addReaction(messageId: Int, emojiName: String)

    fun removeReaction(messageId: Int, emojiName: String)
}
