package com.secondslot.coursework.di

import android.content.Context
import androidx.room.Room
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.domain.usecase.GetOwnProfileUseCase
import com.secondslot.coursework.domain.usecase.GetProfileUseCase
import com.secondslot.coursework.features.profile.presenter.ProfilePresenter

class GlobalDI private constructor(private val applicationContext: Context) {

    val appDatabase = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    ).build()

    val getProfileUseCase by lazy { GetProfileUseCase() }

    val getOwnProfileUseCase by lazy { GetOwnProfileUseCase() }

    val profilePresenter by lazy {
        ProfilePresenter(
            getProfileUseCase,
            getOwnProfileUseCase
        )
    }

    companion object {
        lateinit var INSTANCE: GlobalDI

        fun init(applicationContext: Context) {
            INSTANCE = GlobalDI(applicationContext)
        }
    }
}
