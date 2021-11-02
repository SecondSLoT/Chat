package com.secondslot.coursework.data.db

import androidx.annotation.WorkerThread
import com.secondslot.coursework.data.db.model.MessageEntity
import com.secondslot.coursework.domain.model.Reaction
import java.util.*

object MessagesSource {

    private val messages: ArrayList<MessageEntity> = arrayListOf(
        MessageEntity(
            messageId = UUID.randomUUID(),
            userId = 1,
            datetime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 20,
            username = "Darrel Steward",
            userPhoto = "test_image.png",
            message = "Hi"
        ),
        MessageEntity(
            messageId = UUID.randomUUID(),
            userId = 1,
            datetime = System.currentTimeMillis() - 5000L,
            username = "Darrel Steward",
            message = "How are you?",
            userPhoto = "test_image.png",
            reactions = arrayListOf(
                Reaction(ReactionsSource.reactions[3].code, 1, isSelected = true),
            )
        )
    )

    @WorkerThread
    fun getMessages(channelId: Int): ArrayList<MessageEntity> {
        return messages
    }
}
