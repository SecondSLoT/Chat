package com.secondslot.coursework.features.chat.ui

import com.secondslot.coursework.data.local.model.ReactionLocal

interface ChooseReactionListener {

    fun reactionChosen(reaction: ReactionLocal)
}
