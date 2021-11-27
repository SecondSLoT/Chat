package com.secondslot.coursework.domain.usecase

import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
) {

    fun execute(
        anchor: String = "newest",
        numBefore: String = "0",
        numAfter: String = "0",
        narrow: Map<String, Any>
    ): Observable<List<Message>> {
        return messagesRepository.getMessages(anchor, numBefore, numAfter, narrow)
    }
}
