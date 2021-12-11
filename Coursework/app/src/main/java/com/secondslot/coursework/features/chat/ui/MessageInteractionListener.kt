package com.secondslot.coursework.features.chat.ui

import com.secondslot.coursework.features.chat.model.MessageItem

interface MessageInteractionListener {

    fun messageOnLongClick(message: MessageItem)

    fun onAddReactionButtonClick(message: MessageItem)

    fun addReaction(messageId: Int, emojiName: String)

    fun removeReaction(messageId: Int, emojiName: String)
}
