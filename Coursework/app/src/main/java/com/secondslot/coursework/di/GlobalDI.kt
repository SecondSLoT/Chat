package com.secondslot.coursework.di

import android.content.Context
import androidx.room.Room
import com.secondslot.coursework.data.db.AppDatabase

class GlobalDI private constructor(private val applicationContext: Context) {

    val appDatabase = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    ).build()

    companion object {
        lateinit var INSTANCE: GlobalDI

        fun init(applicationContext: Context) {
            INSTANCE = GlobalDI(applicationContext)
        }
    }
}
