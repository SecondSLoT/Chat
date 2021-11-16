package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.data.repository.MessagesRepositoryImpl
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Observable

class GetMessagesUseCase {

    private val repository: MessagesRepository = MessagesRepositoryImpl()

    fun execute(
        anchor: String = "newest",
        numBefore: String = "20",
        numAfter: String = "0",
        narrow: Map<String, Any>
    ): Observable<List<Message>> {
        return repository.getMessages(anchor, numBefore, numAfter, narrow)
    }
}
