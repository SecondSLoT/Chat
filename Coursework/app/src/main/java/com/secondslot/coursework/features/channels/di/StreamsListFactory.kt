package com.secondslot.coursework.features.channels.di

import com.secondslot.coursework.features.channels.presenter.StreamsListPresenter
import dagger.assisted.AssistedFactory

@AssistedFactory
interface StreamsListFactory {
    fun create(viewType: String): StreamsListPresenter
}
