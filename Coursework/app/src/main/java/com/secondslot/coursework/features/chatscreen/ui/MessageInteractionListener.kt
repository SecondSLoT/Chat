package com.secondslot.coursework.features.chatscreen.ui

import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.model.Reaction

interface MessageInteractionListener {

    fun openReactionsSheet(message: Message)

    fun removeReaction(message: Message, reaction: Reaction)
}
