package com.secondslot.coursework.features.people.di

import com.secondslot.coursework.di.AppComponent
import com.secondslot.coursework.features.people.ui.UsersFragment
import dagger.Component

@UsersScope
@Component(
    modules = [UsersModule::class],
    dependencies = [AppComponent::class]
)
interface UsersComponent {

    fun inject(usersFragment: UsersFragment)

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent
        ): UsersComponent
    }
}
