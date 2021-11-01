package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.domain.model.ExpandableChannelModel
import io.reactivex.Observable

interface ChannelsRepository {

    fun getChannels(): Observable<List<ExpandableChannelModel>>

    fun getAllStreams(): Observable<List<ExpandableChannelModel>>
}
