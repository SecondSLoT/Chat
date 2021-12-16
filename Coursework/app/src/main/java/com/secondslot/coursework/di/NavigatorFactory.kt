package com.secondslot.coursework.di

import androidx.fragment.app.FragmentActivity
import com.secondslot.coursework.navigation.Navigator
import dagger.assisted.AssistedFactory

@AssistedFactory
interface NavigatorFactory {
    fun create(activity: FragmentActivity): Navigator
}
