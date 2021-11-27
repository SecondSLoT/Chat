package com.secondslot.coursework

import android.app.Application
import com.secondslot.coursework.di.AppComponent
import com.secondslot.coursework.di.DaggerAppComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory().create(applicationContext)
    }

    companion object {
        lateinit var appComponent: AppComponent
            private set
    }
}
