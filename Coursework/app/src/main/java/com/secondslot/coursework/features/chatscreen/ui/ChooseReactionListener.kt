package com.secondslot.coursework.features.chatscreen.ui

import com.secondslot.coursework.domain.model.Reaction

interface ChooseReactionListener {

    fun reactionChosen(reaction: Reaction)
}
