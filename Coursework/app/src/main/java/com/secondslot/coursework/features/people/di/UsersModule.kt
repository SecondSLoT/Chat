package com.secondslot.coursework.features.people.di

import com.secondslot.coursework.features.people.presenter.UsersContract
import com.secondslot.coursework.features.people.presenter.UsersPresenter
import dagger.Binds
import dagger.Module

@Module
interface UsersModule {

    @UsersScope
    @Binds
    fun bindUsersPresenter(
        impl: UsersPresenter
    ): UsersContract.UsersPresenter
}
