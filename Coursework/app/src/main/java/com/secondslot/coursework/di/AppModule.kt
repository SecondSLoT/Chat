package com.secondslot.coursework.di

import android.content.ClipboardManager
import android.content.Context
import androidx.room.Room
import com.secondslot.coursework.data.api.AuthorizationInterceptor
import com.secondslot.coursework.data.api.ZulipApiService
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.other.MyClipboardManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideZulipApiService(authorizationClient: OkHttpClient): ZulipApiService {
        return Retrofit.Builder()
            .baseUrl(ZulipApiService.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(authorizationClient)
            .build()
            .create(ZulipApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthorizationClient(
        authorizationInterceptor: AuthorizationInterceptor
    ): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(authorizationInterceptor)
        .build()

    @Provides
    fun provideClipboardManager(context: Context): ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    @Provides
    fun provideMyClipboardManager(clipboard: ClipboardManager): MyClipboardManager =
        MyClipboardManager(clipboard)
}
