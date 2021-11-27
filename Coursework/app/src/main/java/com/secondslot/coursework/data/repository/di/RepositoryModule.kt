package com.secondslot.coursework.data.repository.di

import com.secondslot.coursework.data.repository.MessagesRepositoryImpl
import com.secondslot.coursework.data.repository.ReactionsRepositoryImpl
import com.secondslot.coursework.data.repository.StreamsRepositoryImpl
import com.secondslot.coursework.data.repository.UsersRepositoryImpl
import com.secondslot.coursework.domain.repository.MessagesRepository
import com.secondslot.coursework.domain.repository.ReactionsRepository
import com.secondslot.coursework.domain.repository.StreamsRepository
import com.secondslot.coursework.domain.repository.UsersRepository
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    fun bindMessageRepository(impl: MessagesRepositoryImpl): MessagesRepository

    @Binds
    fun bindReactionsRepository(impl: ReactionsRepositoryImpl): ReactionsRepository

    @Binds
    fun bindStreamsRepository(impl: StreamsRepositoryImpl): StreamsRepository

    @Binds
    fun bindUsersRepository(impl: UsersRepositoryImpl): UsersRepository
}
