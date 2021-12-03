package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import javax.inject.Inject

class GetStreamByIdUseCase @Inject constructor(
    private val streamsRepository: StreamsRepository

) {

    suspend fun execute(streamId: Int): Stream {
        return streamsRepository.getStreamById(streamId)
    }
}
