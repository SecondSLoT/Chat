package com.secondslot.coursework.data.api

import com.secondslot.coursework.data.api.model.StreamsResponse
import com.secondslot.coursework.data.api.model.TopicsResponse
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ZulipApiService {

    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Observable<StreamsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") streamId: Int): Observable<TopicsResponse>

    companion object {
        private const val BASE_URL = "https://tinkoff-android-fall21.zulipchat.com/api/v1/"

        fun create(): ZulipApiService {
            val authorizationClient = OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(authorizationClient)
                .build()
                .create(ZulipApiService::class.java)
        }
    }
}
