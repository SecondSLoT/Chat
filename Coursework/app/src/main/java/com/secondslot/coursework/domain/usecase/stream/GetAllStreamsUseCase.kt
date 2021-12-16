package com.secondslot.coursework.domain.usecase.stream

import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetAllStreamsUseCase @Inject constructor(
    private val streamsRepository: StreamsRepository
) {

    fun execute(searchQuery: String = ""): Observable<List<Stream>> {
        return if (searchQuery.isEmpty()) {
            streamsRepository.getAllStreams()
        } else {
            streamsRepository.getAllStreams()
                .map { channels ->
                    channels.filter {
                        it.streamName.contains(searchQuery, ignoreCase = true)
                    }
                }
        }
    }
}
