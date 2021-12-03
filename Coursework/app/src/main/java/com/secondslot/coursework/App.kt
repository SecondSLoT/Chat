package com.secondslot.coursework

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.preference.PreferenceManager
import com.secondslot.coursework.di.AppComponent
import com.secondslot.coursework.di.DaggerAppComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val isDarkTheme = prefs.getBoolean("dark_theme", false)
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }

        appComponent = DaggerAppComponent.factory().create(applicationContext)
    }

    companion object {
        lateinit var appComponent: AppComponent
            private set
    }
}
