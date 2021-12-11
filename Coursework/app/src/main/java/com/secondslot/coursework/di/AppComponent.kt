package com.secondslot.coursework.di

import android.content.Context
import com.secondslot.coursework.data.repository.di.RepositoryModule
import com.secondslot.coursework.domain.repository.MessagesRepository
import com.secondslot.coursework.domain.repository.ReactionsRepository
import com.secondslot.coursework.domain.repository.StreamsRepository
import com.secondslot.coursework.domain.repository.UsersRepository
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, RepositoryModule::class])
interface AppComponent {

    fun provideMessagesRepository(): MessagesRepository

    fun provideReactionsRepository(): ReactionsRepository

    fun provideStreamsRepository(): StreamsRepository

    fun provideUsersRepository(): UsersRepository

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): AppComponent
    }
}
