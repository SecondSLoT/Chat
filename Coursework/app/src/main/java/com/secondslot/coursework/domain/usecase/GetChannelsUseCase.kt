package com.secondslot.coursework.domain.usecase

import android.util.Log
import com.secondslot.coursework.data.repository.ChannelsRepositoryImpl
import com.secondslot.coursework.domain.model.ExpandableChannelModel
import com.secondslot.coursework.domain.repository.ChannelsRepository
import io.reactivex.Observable

class GetChannelsUseCase {

    private val channelsRepository: ChannelsRepository = ChannelsRepositoryImpl()

    fun execute(searchQuery: String = ""): Observable<List<ExpandableChannelModel>> {
        return if (searchQuery.isEmpty()) {
            channelsRepository.getChannels()
        } else {
            channelsRepository.getChannels()
                .map { channels ->
                    channels.filter {
                        it.channelGroup.groupTitle.contains(searchQuery, ignoreCase = true)
                    }
                }
        }
    }
}
