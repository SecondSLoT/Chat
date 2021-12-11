package com.secondslot.coursework.features.profile.di

import com.secondslot.coursework.di.AppComponent
import com.secondslot.coursework.features.profile.ui.ProfileFragment
import dagger.Component

@ProfileScope
@Component(dependencies = [AppComponent::class])
interface ProfileComponent {

    fun inject(profileFragment: ProfileFragment)

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent
        ): ProfileComponent
    }
}
