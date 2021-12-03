package com.secondslot.coursework.features.chat.di

import androidx.lifecycle.ViewModelProvider
import com.secondslot.coursework.features.chat.vm.ChatViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ChatModule {

    @ChatScope
    @Binds
    fun bindChatViewModelFactory(impl: ChatViewModelFactory): ViewModelProvider.Factory
}
