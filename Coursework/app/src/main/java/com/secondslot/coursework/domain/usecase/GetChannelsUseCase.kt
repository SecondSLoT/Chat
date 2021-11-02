package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.ChannelsRepositoryImpl
import com.secondslot.coursework.domain.model.ChannelGroup
import com.secondslot.coursework.domain.repository.ChannelsRepository
import io.reactivex.Observable

class GetChannelsUseCase {

    private val channelsRepository: ChannelsRepository = ChannelsRepositoryImpl()

    fun execute(searchQuery: String = ""): Observable<List<ChannelGroup>> {
        return if (searchQuery.isEmpty()) {
            channelsRepository.getChannels()
        } else {
            channelsRepository.getChannels()
                .map { channels ->
                    channels.filter {
                        it.groupTitle.contains(searchQuery, ignoreCase = true)
                    }
                }
        }
    }
}
