package com.secondslot.coursework.data.api

import com.secondslot.coursework.data.api.model.UserRemote
import com.secondslot.coursework.data.api.model.response.AllStreamsResponse
import com.secondslot.coursework.data.api.model.response.AllUsersResponse
import com.secondslot.coursework.data.api.model.response.MessagesResponse
import com.secondslot.coursework.data.api.model.response.SendResponse
import com.secondslot.coursework.data.api.model.response.SubscriptionsResponse
import com.secondslot.coursework.data.api.model.response.TopicsResponse
import com.secondslot.coursework.data.api.model.response.UserResponse
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ZulipApiService {

    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Observable<SubscriptionsResponse>

    @GET("streams")
    fun getAllStreams(): Observable<AllStreamsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") streamId: Int): Observable<TopicsResponse>

    @GET("users")
    fun getAllUsers(): Observable<AllUsersResponse>

    @GET("users/{user_id}")
    fun getUser(@Path("user_id") userId: Int): Single<UserResponse>

    @GET("users/me")
    fun getOwnUser(): Single<UserRemote>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: String,
        @Query("num_after") numAfter: String,
        @Query("narrow") narrow: String
    ): Observable<MessagesResponse>

    @POST("messages")
    fun sendMessage(
        @Query("type") type: String,
        @Query("to") streamId: Int,
        @Query("topic") topicName: String,
        @Query("content") messageText: String
    ): Single<SendResponse>

    @POST("messages/{message_id}/reactions")
    fun addReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): Single<SendResponse>

    @DELETE("messages/{message_id}/reactions")
    fun removeReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): Single<SendResponse>

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
