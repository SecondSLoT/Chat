package com.secondslot.coursework.features.channels.di

import com.secondslot.coursework.di.AppComponent
import com.secondslot.coursework.features.channels.ui.StreamsFragment
import com.secondslot.coursework.features.channels.ui.StreamsListFragment
import dagger.Component

@StreamsScope
@Component(
    modules = [StreamsModule::class],
    dependencies = [AppComponent::class]
)
interface StreamsComponent {

    fun injectStreamsFragment(streamsFragment: StreamsFragment)

    fun injectStreamsListFragment(streamsListFragment: StreamsListFragment)

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent
        ): StreamsComponent
    }
}
