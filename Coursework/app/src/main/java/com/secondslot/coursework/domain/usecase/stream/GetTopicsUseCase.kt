package com.secondslot.coursework.domain.usecase.stream

import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetTopicsUseCase @Inject constructor(
    private val streamsRepository: StreamsRepository

) {

    fun execute(streamId: Int): Observable<List<Stream.Topic>> {
        return streamsRepository.getTopics(streamId)
    }
}
