package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetStreamByIdUseCase @Inject constructor(
    private val streamsRepository: StreamsRepository

) {

    fun execute(streamId: Int): Observable<Stream> {
        return streamsRepository.getStreamById(streamId)
    }
}
