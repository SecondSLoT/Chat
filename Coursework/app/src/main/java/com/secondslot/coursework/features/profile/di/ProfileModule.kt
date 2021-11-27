package com.secondslot.coursework.features.profile.di

import com.secondslot.coursework.features.profile.presenter.ProfileContract
import com.secondslot.coursework.features.profile.presenter.ProfilePresenter
import dagger.Binds
import dagger.Module

@Module
interface ProfileModule {

    @ProfileScope
    @Binds
    fun bindProfilePresenter(
        impl: ProfilePresenter
    ): ProfileContract.ProfilePresenter
}
