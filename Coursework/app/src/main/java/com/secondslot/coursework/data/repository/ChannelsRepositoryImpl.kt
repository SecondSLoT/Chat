package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.api.StreamsSource
import com.secondslot.coursework.data.db.ChannelsSource
import com.secondslot.coursework.data.db.model.toDomainModel
import com.secondslot.coursework.domain.model.ChannelGroup
import com.secondslot.coursework.domain.repository.ChannelsRepository
import com.secondslot.coursework.util.Temporary
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ChannelsRepositoryImpl : ChannelsRepository {

    override fun getChannels(): Observable<List<ChannelGroup>> {
        return Observable.create<List<ChannelGroup>> { emitter ->
            if (Temporary.imitateError()) {
                emitter.onError(Throwable("Error getting channels list"))
            } else {
                emitter.onNext(ChannelsSource.getChannels().map { it.toDomainModel() })
            }
        }
            .delay(1000L, TimeUnit.MILLISECONDS)
    }

    override fun getAllStreams(): Observable<List<ChannelGroup>> {
        return Observable.create<List<ChannelGroup>> { emitter ->
            if (Temporary.imitateError()) {
                emitter.onError(Throwable("Error getting streams list"))
            } else {
                emitter.onNext(StreamsSource.getAllStreams().map { it.toDomainModel() })
            }
        }
            .delay(1000L, TimeUnit.MILLISECONDS)
    }
}
