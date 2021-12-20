package com.secondslot.coursework.features.profile.di

import com.secondslot.coursework.features.profile.presenter.ProfilePresenter
import dagger.assisted.AssistedFactory

@AssistedFactory
interface ProfilePresenterFactory {
    fun create(
        userId: Int
    ): ProfilePresenter
}
