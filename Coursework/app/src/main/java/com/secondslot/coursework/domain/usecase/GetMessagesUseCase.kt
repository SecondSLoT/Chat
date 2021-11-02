package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.MessagesRepositoryImpl
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Observable

class GetMessagesUseCase {

    private val repository: MessagesRepository = MessagesRepositoryImpl()

    fun execute(channelId: Int): Observable<List<Message>> {
        return repository.getMessages(channelId)
    }
}
