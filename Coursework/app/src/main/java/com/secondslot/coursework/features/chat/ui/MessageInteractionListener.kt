package com.secondslot.coursework.features.chat.ui

import com.secondslot.coursework.domain.model.Reaction
import com.secondslot.coursework.features.chat.model.MessageItem

interface MessageInteractionListener {

    fun openReactionsSheet(message: MessageItem)

    fun removeReaction(message: MessageItem, reaction: Reaction)
}
