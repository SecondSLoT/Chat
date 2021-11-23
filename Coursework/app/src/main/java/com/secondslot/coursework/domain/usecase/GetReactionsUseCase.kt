package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.domain.repository.ReactionsRepository

class GetReactionsUseCase(
    private val reactionsRepository: ReactionsRepository
) {

    fun execute(): List<ReactionLocal> {
        return reactionsRepository.getReactions()
    }
}
