package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.db.MessagesSource
import com.secondslot.coursework.data.db.model.toDomainModel
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class MessagesRepositoryImpl : MessagesRepository {

    override fun getMessages(channelId: Int): Observable<List<Message>> {
        return Observable.fromCallable {
            MessagesSource.getMessages(channelId).map { it.toDomainModel() }
        }
            .delay(1000L, TimeUnit.MILLISECONDS)
    }
}
