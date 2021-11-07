package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.StreamsRepositoryImpl
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import io.reactivex.Observable

class GetAllStreamsUseCase {

    private val streamsRepository: StreamsRepository = StreamsRepositoryImpl()

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
