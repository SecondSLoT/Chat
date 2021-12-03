package com.secondslot.coursework.data.api

import com.secondslot.coursework.data.api.model.UserRemote
import com.secondslot.coursework.data.api.model.response.AllStreamsResponse
import com.secondslot.coursework.data.api.model.response.AllUsersResponse
import com.secondslot.coursework.data.api.model.response.MessagesResponse
import com.secondslot.coursework.data.api.model.response.SendResponse
import com.secondslot.coursework.data.api.model.response.SubscriptionsResponse
import com.secondslot.coursework.data.api.model.response.TopicsResponse
import com.secondslot.coursework.data.api.model.response.UserResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ZulipApiService {

    @GET("users/me/subscriptions")
    suspend fun getSubscribedStreams(): SubscriptionsResponse

    @GET("streams")
    suspend fun getAllStreams(): AllStreamsResponse

    @GET("users/me/{stream_id}/topics")
    suspend fun getTopics(@Path("stream_id") streamId: Int): TopicsResponse

    @GET("users")
    suspend fun getAllUsers(): AllUsersResponse

    @GET("users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: Int): UserResponse

    @GET("users/me")
    suspend fun getOwnUser(): UserRemote

    @GET("messages")
    suspend fun getMessages(
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: String,
        @Query("num_after") numAfter: String,
        @Query("narrow") narrow: String
    ): MessagesResponse

    @POST("messages")
    suspend fun sendMessage(
        @Query("type") type: String,
        @Query("to") streamId: Int,
        @Query("topic") topicName: String,
        @Query("content") messageText: String
    ): SendResponse

    @POST("messages/{message_id}/reactions")
    suspend fun addReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): SendResponse

    @DELETE("messages/{message_id}/reactions")
    suspend fun removeReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): SendResponse

    companion object {
        const val BASE_URL = "https://tinkoff-android-fall21.zulipchat.com/api/v1/"
    }
}
