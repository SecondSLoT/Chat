package com.secondslot.coursework.data.repository

import com.secondslot.coursework.data.db.MessagesSource
import com.secondslot.coursework.domain.model.ChatItem
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class MessagesRepositoryImpl : MessagesRepository {

    override fun getMessages(channelId: Int): Observable<ArrayList<ChatItem>> {
        return Observable.fromCallable { MessagesSource.getMessages(channelId) }
            .delay(1000L, TimeUnit.MILLISECONDS)
    }
}
