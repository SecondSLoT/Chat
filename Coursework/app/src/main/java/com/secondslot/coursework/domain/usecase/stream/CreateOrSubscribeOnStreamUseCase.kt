package com.secondslot.coursework.domain.usecase.stream

import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.data.api.model.response.toServerResult
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Single
import javax.inject.Inject

class CreateOrSubscribeOnStreamUseCase @Inject constructor(
    private val streamsRepository: StreamsRepository
) {

    fun execute(
        subscriptions: Map<String, Any>,
        announce: Boolean = false
    ): Single<ServerResult> {
        return streamsRepository.createOrSubscribeOnStream(subscriptions, announce)
            .map { it.toServerResult()}
    }
}
