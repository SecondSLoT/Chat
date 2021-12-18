package com.secondslot.coursework.data.api

import com.secondslot.coursework.data.api.model.MessageRemote
import com.secondslot.coursework.data.api.model.StreamRemote
import com.secondslot.coursework.data.api.model.StreamWithTopicsRemote
import com.secondslot.coursework.data.api.model.UserRemote
import com.secondslot.coursework.data.api.model.response.ServerResponse
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class NetworkManager @Inject constructor(
    private val apiService: ZulipApiService
    ) {

    fun getSubscribedStreams(): Observable<List<StreamWithTopicsRemote>> {
        return mapStreams(apiService.getSubscribedStreams()
            // Convert SubscriptionsResponse to List<StreamRemote>
            .map { it.streams }
        )
    }

    fun getAllStreams(): Observable<List<StreamWithTopicsRemote>> {
        return mapStreams(apiService.getAllStreams()
            // Convert AllStreamsResponse to List<StreamRemote>
            .map { it.streams }
        )
    }

    private fun mapStreams(
        streamsObservable: Observable<List<StreamRemote>>
    ): Observable<List<StreamWithTopicsRemote>> {
        // Convert List<StreamRemote> to StreamRemote
        return streamsObservable
            .flatMap { streamRemoteList -> Observable.fromIterable(streamRemoteList) }
            .flatMap(
                // Load List<TopicRemote> for certain StreamRemote
                { streamRemote -> apiService.getTopics(streamRemote.id).map { it.topics } },
                // Create StreamWithTopicsRemote and set StreamRemote and List<TopicRemote> to it
                { streamRemote, topics ->
                    StreamWithTopicsRemote(
                        streamRemote = streamRemote,
                        topics = topics
                    )
                }
            )
            // Gather all StreamWithTopicsRemote in one list
            .toList()
            // Convert Single<List<StreamWithTopicsRemote>> to Observable
            .toObservable()
    }

    fun getAllUsers(): Observable<List<UserRemote>> =
        apiService.getAllUsers().map { it.users.sortedBy { user -> user.fullName } }

    fun getUser(userId: Int): Observable<List<UserRemote>> =
        apiService.getUser(userId).map { listOf(it.user) }

    fun getOwnUser(): Observable<List<UserRemote>> = apiService.getOwnUser().map { listOf(it) }

    fun getMessages(
        anchor: String,
        numBefore: String,
        numAfter: String,
        narrow: Map<String, Any>
    ): Observable<List<MessageRemote>> {
        val narrowValue = generateNarrowValue(narrow)
        return apiService.getMessages(anchor, numBefore, numAfter, narrowValue)
            .map { it.messages }
    }

    private fun generateNarrowValue(parameters: Map<String, Any>): String {
        val result = StringBuilder("[")

        parameters.forEach {
            result.append("{\"operator\": \"${it.key}\", \"operand\": ")
            if (it.value is Int || it.value is Boolean) {
                result.append("${it.value}}, ")
            } else {
                result.append("\"${it.value}\"}, ")
            }
        }

        result.run {
            delete(result.length - 2, result.length)
            append("]")
        }

        return result.toString()
    }

    fun createOrSubscribeOnStream(
        subscriptions: Map<String, Any>,
        announce: Boolean
    ): Single<ServerResponse> {
        val subscriptionsValue = generateSubscriptionsValue(subscriptions)
        return apiService.createOrSubscribeOnStream(subscriptionsValue, announce)
    }

    private fun generateSubscriptionsValue(parameters: Map<String, Any>): String {
        val result = StringBuilder("[{")

        parameters.forEach {
            result.append("\"${it.key}\": \"${it.value}\", ")
        }

        result.run {
            delete(result.length - 2, result.length)
            append("}]")
        }

        return result.toString()
    }

    fun sendMessage(
        type: String,
        streamId: Int,
        topicName: String,
        messageText: String
    ): Single<ServerResponse> {
        return apiService.sendMessage(type, streamId, topicName, messageText)
    }

    fun addReaction(
        messageId: Int,
        emojiName: String
    ): Single<ServerResponse> {
        return apiService.addReaction(messageId, emojiName)
    }

    fun removeReaction(
        messageId: Int,
        emojiName: String
    ): Single<ServerResponse> {
        return apiService.removeReaction(messageId, emojiName)
    }

    fun deleteMessage(
        messageId: Int
    ): Single<ServerResponse> {
        return apiService.deleteMessage(messageId)
    }

    fun editMessage(
        messageId: Int,
        newMessageText: String
    ): Single<ServerResponse> {
        return apiService.editMessage(messageId, newMessageText)
    }

    fun moveMessage(
        messageId: Int,
        newTopic: String
    ): Single<ServerResponse> {
        return apiService.moveMessage(messageId, newTopic)
    }

    companion object {
        private const val TAG = "NetworkManager"
    }
}
