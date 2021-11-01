package com.secondslot.coursework.domain.repository

import com.secondslot.coursework.domain.model.ChatItem
import io.reactivex.Observable

interface MessagesRepository {

    fun getMessages(channelId: Int): Observable<ArrayList<ChatItem>>
}
