package com.secondslot.coursework.features.channels.di

import com.secondslot.coursework.features.channels.presenter.StreamsContract
import com.secondslot.coursework.features.channels.presenter.StreamsListContract
import com.secondslot.coursework.features.channels.presenter.StreamsListPresenter
import com.secondslot.coursework.features.channels.presenter.StreamsPresenter
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
    fun bindStreamsListPresenter(
        impl: StreamsListPresenter
    ): StreamsListContract.StreamsListPresenter
}
