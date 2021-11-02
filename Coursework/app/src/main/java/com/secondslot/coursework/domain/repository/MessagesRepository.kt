package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.domain.model.Message
import io.reactivex.Observable

interface MessagesRepository {

    fun getMessages(channelId: Int): Observable<List<Message>>
}
