package com.secondslot.coursework

import android.app.Application
import com.secondslot.coursework.di.GlobalDI

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        GlobalDI.init(this)
    }
}
