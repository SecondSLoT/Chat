package com.secondslot.coursework.di

import android.content.Context
import androidx.room.Room
import com.secondslot.coursework.data.api.AuthorizationInterceptor
import com.secondslot.coursework.data.api.NetworkManager
import com.secondslot.coursework.data.api.ZulipApiService
import com.secondslot.coursework.data.db.AppDatabase
import com.secondslot.coursework.data.repository.MessagesRepositoryImpl
import com.secondslot.coursework.data.repository.ReactionsRepositoryImpl
import com.secondslot.coursework.data.repository.StreamsRepositoryImpl
import com.secondslot.coursework.data.repository.UsersRepositoryImpl
import com.secondslot.coursework.domain.repository.MessagesRepository
import com.secondslot.coursework.domain.repository.ReactionsRepository
import com.secondslot.coursework.domain.repository.StreamsRepository
import com.secondslot.coursework.domain.repository.UsersRepository
import com.secondslot.coursework.domain.usecase.*
import com.secondslot.coursework.features.channels.presenter.StreamsListContract
import com.secondslot.coursework.features.channels.presenter.StreamsListPresenter
import com.secondslot.coursework.features.chat.presenter.ChatContract
import com.secondslot.coursework.features.chat.presenter.ChatPresenter
import com.secondslot.coursework.features.people.presenter.UsersContract
import com.secondslot.coursework.features.people.presenter.UsersPresenter
import com.secondslot.coursework.features.profile.presenter.ProfileContract
import com.secondslot.coursework.features.profile.presenter.ProfilePresenter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class GlobalDI private constructor(applicationContext: Context) {

//    val appDatabase = Room.databaseBuilder(
//        applicationContext,
//        AppDatabase::class.java,
//        AppDatabase.DATABASE_NAME
//    ).build()
//
//    val zulipApiService: ZulipApiService by lazy {
//        val authorizationClient = OkHttpClient.Builder()
//            .addInterceptor(AuthorizationInterceptor())
//            .build()
//
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(MoshiConverterFactory.create())
//            .client(authorizationClient)
//            .build()
//            .create(ZulipApiService::class.java)
//    }

//    val networkManager by lazy {
//        NetworkManager(zulipApiService)
//    }

//    val addReactionUseCase by lazy {
//        AddReactionUseCase(reactionsRepository)
//    }
//    val getAllStreamsUseCase by lazy {
//        GetAllStreamsUseCase(streamsRepository)
//    }
//    val getAllUsersUseCase by lazy {
//        GetAllUsersUseCase(usersRepository)
//    }
//    val getMessagesUseCase by lazy {
//        GetMessagesUseCase(messagesRepository)
//    }
//    val getOwnProfileUseCase by lazy {
//        GetOwnProfileUseCase(usersRepository)
//    }
//    val getProfileUseCase by lazy {
//        GetProfileUseCase(usersRepository)
//    }
//    val getReactionsUseCase by lazy {
//        GetReactionsUseCase(reactionsRepository)
//    }
//    val getSubscribedStreamsUseCase by lazy {
//        GetSubscribedStreamsUseCase(streamsRepository)
//    }
//    val removeReactionUseCase by lazy {
//        RemoveReactionUseCase(reactionsRepository)
//    }
//    val sendMessagesUseCase by lazy {
//        SendMessageUseCase(messagesRepository)
//    }

//    val messagesRepository: MessagesRepository by lazy {
//        MessagesRepositoryImpl(appDatabase, networkManager)
//    }
//    val reactionsRepository: ReactionsRepository by lazy {
//        ReactionsRepositoryImpl(networkManager)
//    }
//    val streamsRepository: StreamsRepository by lazy {
//        StreamsRepositoryImpl(appDatabase, networkManager)
//    }
//    val usersRepository: UsersRepository by lazy {
//        UsersRepositoryImpl(appDatabase, networkManager)
//    }

//    fun getChatPresenter(): ChatContract.ChatPresenter {
//        return ChatPresenter(
//            getMessagesUseCase,
//            sendMessagesUseCase,
//            getOwnProfileUseCase,
//            addReactionUseCase,
//            removeReactionUseCase,
//            getReactionsUseCase
//        )
//    }
//
//    fun  getUsersPresenter(): UsersContract.UsersPresenter {
//        return UsersPresenter(
//            getAllUsersUseCase
//        )
//    }
//
//    fun getProfilePresenter(): ProfileContract.ProfilePresenter {
//        return ProfilePresenter(
//            getProfileUseCase,
//            getOwnProfileUseCase
//        )
//    }
//
//    fun getStreamsListPresenter(): StreamsListContract.StreamsListPresenter {
//        return StreamsListPresenter(
//            getSubscribedStreamsUseCase,
//            getAllStreamsUseCase
//        )
//    }

//    companion object {
//        private const val BASE_URL = "https://tinkoff-android-fall21.zulipchat.com/api/v1/"
//
//        lateinit var INSTANCE: GlobalDI
//
//        fun init(applicationContext: Context) {
//            INSTANCE = GlobalDI(applicationContext)
//        }
//    }
}
