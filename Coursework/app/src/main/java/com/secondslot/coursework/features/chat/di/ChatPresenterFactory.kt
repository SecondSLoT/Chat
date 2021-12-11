package com.secondslot.coursework.features.chat.di

import com.secondslot.coursework.features.chat.presenter.ChatPresenter
import dagger.assisted.AssistedFactory

@AssistedFactory
interface ChatPresenterFactory {
    fun create(
        streamId: Int,
        topicName: String
    ): ChatPresenter
}
