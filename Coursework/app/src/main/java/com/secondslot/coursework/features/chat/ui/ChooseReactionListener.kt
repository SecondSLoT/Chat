package com.secondslot.coursework.features.chat.ui

import com.secondslot.coursework.domain.model.Reaction

interface ChooseReactionListener {

    fun reactionChosen(reaction: Reaction)
}
