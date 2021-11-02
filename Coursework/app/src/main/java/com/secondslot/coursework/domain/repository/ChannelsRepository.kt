package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.domain.model.ChannelGroup
import io.reactivex.Observable

interface ChannelsRepository {

    fun getChannels(): Observable<List<ChannelGroup>>

    fun getAllStreams(): Observable<List<ChannelGroup>>
}
