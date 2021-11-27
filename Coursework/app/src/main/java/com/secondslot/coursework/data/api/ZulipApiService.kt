package com.secondslot.coursework.data.api

import com.secondslot.coursework.data.api.model.UserRemote
import com.secondslot.coursework.data.api.model.response.*
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*

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
    fun getUser(@Path("user_id") userId: Int): Observable<UserResponse>

    @GET("users/me")
    fun getOwnUser(): Observable<UserRemote>

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
        const val BASE_URL = "https://tinkoff-android-fall21.zulipchat.com/api/v1/"
    }
}
