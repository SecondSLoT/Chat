package com.secondslot.coursework.features.chat.listener

import com.secondslot.coursework.data.local.model.ReactionLocal

interface ChooseReactionListener {

    fun reactionChosen(reaction: ReactionLocal)
}
