package com.secondslot.coursework.features.channels.di

import com.secondslot.coursework.features.channels.presenter.StreamsListPresenter
import dagger.assisted.AssistedFactory

@AssistedFactory
interface StreamsListPresenterFactory {
    fun create(viewType: String): StreamsListPresenter
}
