package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.repository.StreamsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllStreamsUseCase @Inject constructor(
    private val streamsRepository: StreamsRepository
) {

    fun execute(searchQuery: String = ""): Flow<List<Stream>> {
        return if (searchQuery.isEmpty()) {
            streamsRepository.getAllStreams()
        } else {
            streamsRepository.getAllStreams().map { channels ->
                channels.filter { it.streamName.contains(searchQuery, ignoreCase = true) }
            }
        }
    }
}
