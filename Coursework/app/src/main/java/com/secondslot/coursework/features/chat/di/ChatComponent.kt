package com.secondslot.coursework.features.chat.di

import com.secondslot.coursework.di.AppComponent
import com.secondslot.coursework.features.chat.ui.ChatFragment
import dagger.Component

@ChatScope
@Component(dependencies = [AppComponent::class])
interface ChatComponent {

    fun inject(chatFragment: ChatFragment)

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent
        ): ChatComponent
    }
}
