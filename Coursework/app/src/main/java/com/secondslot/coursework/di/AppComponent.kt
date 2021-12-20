package com.secondslot.coursework.di

import android.content.Context
import com.secondslot.coursework.data.repository.di.RepositoryModule
import com.secondslot.coursework.domain.repository.MessagesRepository
import com.secondslot.coursework.domain.repository.ReactionsRepository
import com.secondslot.coursework.domain.repository.StreamsRepository
import com.secondslot.coursework.domain.repository.UsersRepository
import com.secondslot.coursework.features.main.ui.MainActivity
import com.secondslot.coursework.features.main.ui.MainFragment
import com.secondslot.coursework.other.MyClipboardManager
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

    fun provideMyClipboardManager(): MyClipboardManager

    fun injectMainActivity(mainActivity: MainActivity)

    fun injectMainFragment(mainFragment: MainFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): AppComponent
    }
}
