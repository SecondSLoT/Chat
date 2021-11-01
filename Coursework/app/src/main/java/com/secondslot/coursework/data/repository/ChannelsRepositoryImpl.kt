package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.db.ChannelsSource
import com.secondslot.coursework.domain.model.ExpandableChannelModel
import com.secondslot.coursework.domain.repository.ChannelsRepository
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ChannelsRepositoryImpl : ChannelsRepository {

    override fun getChannels(): Observable<List<ExpandableChannelModel>> {
        return Observable.create<List<ExpandableChannelModel>> { emitter ->
            if (Random.nextBoolean()) emitter.onNext(ChannelsSource.getChannels())
            else emitter.onError(Throwable("Error getting channels list"))
        }
            .delay(1000L, TimeUnit.MILLISECONDS)
    }

    override fun getAllStreams(): Observable<List<ExpandableChannelModel>> {
        return Observable.create<List<ExpandableChannelModel>> { emitter ->
            if (Random.nextBoolean()) emitter.onNext(ChannelsSource.getChannels())
            else emitter.onError(Throwable("Error getting streams list"))
        }
            .delay(1000L, TimeUnit.MILLISECONDS)
    }
}
