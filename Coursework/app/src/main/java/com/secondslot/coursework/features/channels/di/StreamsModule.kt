package com.secondslot.coursework.features.channels.di

import androidx.lifecycle.ViewModelProvider
import com.secondslot.coursework.features.channels.vm.*
import dagger.Binds
import dagger.Module

@Module
interface StreamsModule {

    @StreamsScope
    @Binds
    fun bindStreamsPresenter(
        impl: StreamsPresenter
    ): StreamsContract.StreamsPresenter

    @StreamsScope
    @Binds
    fun bindStreamsListViewModelFactory(
        impl: StreamsListViewModelFactory
    ): ViewModelProvider.Factory
}
