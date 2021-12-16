package com.secondslot.coursework.domain.usecase.reaction

import com.secondslot.coursework.data.local.model.ReactionLocal
import com.secondslot.coursework.domain.repository.ReactionsRepository
import javax.inject.Inject

class GetReactionsUseCase @Inject constructor(
    private val reactionsRepository: ReactionsRepository
) {

    fun execute(): List<ReactionLocal> {
        return reactionsRepository.getReactions()
    }
}
