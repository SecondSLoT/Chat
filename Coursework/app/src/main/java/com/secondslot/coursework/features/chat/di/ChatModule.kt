package com.secondslot.coursework.features.chat.di

import com.secondslot.coursework.features.chat.presenter.ChatContract
import com.secondslot.coursework.features.chat.presenter.ChatPresenter
import dagger.Binds
import dagger.Module

@Module
interface ChatModule {

    @ChatScope
    @Binds
    fun bindChatPresenter(impl: ChatPresenter) : ChatContract.ChatPresenter
}
