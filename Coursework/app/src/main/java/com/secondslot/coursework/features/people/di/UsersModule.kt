package com.secondslot.coursework.features.people.di

import androidx.lifecycle.ViewModelProvider
import com.secondslot.coursework.features.people.vm.UsersViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface UsersModule {

    @UsersScope
    @Binds
    fun bindUsersViewModelFactory(
        impl: UsersViewModelFactory
    ): ViewModelProvider.Factory
}
