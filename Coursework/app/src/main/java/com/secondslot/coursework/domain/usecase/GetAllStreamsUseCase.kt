package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.ChannelsRepositoryImpl
import com.secondslot.coursework.domain.model.ExpandableChannelModel
import com.secondslot.coursework.domain.repository.ChannelsRepository
import io.reactivex.Observable

class GetAllStreamsUseCase {

    private val channelsRepository: ChannelsRepository = ChannelsRepositoryImpl()

    fun execute(searchQuery: String = ""): Observable<List<ExpandableChannelModel>> {
        return if (searchQuery.isEmpty()) {
            channelsRepository.getAllStreams()
        } else {
            channelsRepository.getAllStreams()
                .map { channels ->
                    channels.filter {
                        it.channelGroup.groupTitle.contains(searchQuery, ignoreCase = true)
                    }
                }
        }
    }
}
