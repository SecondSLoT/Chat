package com.secondslot.coursework.features.channels.di

import androidx.lifecycle.ViewModelProvider
import com.secondslot.coursework.features.channels.vm.StreamsListViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface StreamsModule {

    @StreamsScope
    @Binds
    fun bindStreamsListViewModelFactory(
        impl: StreamsListViewModelFactory
    ): ViewModelProvider.Factory
}
