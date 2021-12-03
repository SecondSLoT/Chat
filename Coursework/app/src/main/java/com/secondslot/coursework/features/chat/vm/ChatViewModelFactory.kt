package com.secondslot.coursework.features.chat.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.secondslot.coursework.domain.usecase.AddReactionUseCase
import com.secondslot.coursework.domain.usecase.GetMessagesUseCase
import com.secondslot.coursework.domain.usecase.GetOwnProfileUseCase
import com.secondslot.coursework.domain.usecase.GetReactionsUseCase
import com.secondslot.coursework.domain.usecase.GetStreamByIdUseCase
import com.secondslot.coursework.domain.usecase.RemoveReactionUseCase
import com.secondslot.coursework.domain.usecase.SendMessageUseCase
import javax.inject.Inject

class ChatViewModelFactory @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getOwnProfileUseCase: GetOwnProfileUseCase,
    private val addReactionUseCase: AddReactionUseCase,
    private val removeReactionUseCase: RemoveReactionUseCase,
    private val getReactionsUseCase: GetReactionsUseCase,
    private val getStreamByIdUseCase: GetStreamByIdUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(
                getMessagesUseCase,
                sendMessageUseCase,
                getOwnProfileUseCase,
                addReactionUseCase,
                removeReactionUseCase,
                getReactionsUseCase,
                getStreamByIdUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
