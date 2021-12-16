package com.secondslot.coursework.domain.usecase.message

import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.repository.MessagesRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val messagesRepository: MessagesRepository
) {

    fun execute(
        anchor: String,
        numBefore: String,
        numAfter: String,
        narrow: Map<String, Any>
    ): Observable<List<Message>> {
        return messagesRepository.getMessages(anchor, numBefore, numAfter, narrow)
    }
}
