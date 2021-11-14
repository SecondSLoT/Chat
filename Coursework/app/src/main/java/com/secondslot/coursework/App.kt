package com.secondslot.coursework

import android.app.Application
import androidx.room.Room
import com.secondslot.coursework.data.db.AppDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    companion object {
        private lateinit var appDatabase: AppDatabase

        fun getAppDatabase(): AppDatabase {
            return appDatabase
        }
    }
}
