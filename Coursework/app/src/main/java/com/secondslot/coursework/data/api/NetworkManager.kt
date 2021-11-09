package com.secondslot.coursework.data.api

import com.secondslot.coursework.data.api.model.MessageRemote
import com.secondslot.coursework.data.api.model.StreamRemote
import com.secondslot.coursework.data.api.model.StreamWithTopicsRemote
import com.secondslot.coursework.data.api.model.UserRemote
import com.secondslot.coursework.data.api.model.response.SendResponse
import io.reactivex.Observable
import io.reactivex.Single


class NetworkManager {

    private val apiService = ZulipApiService.create()

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

    fun getAllUsers(): Observable<List<UserRemote>> = apiService.getAllUsers().map { it.users }

    fun getUser(userId: Int): Single<UserRemote> = apiService.getUser(userId).map { it.user }

    fun getOwnUser(): Single<UserRemote> = apiService.getOwnUser()

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

    fun sendMessage(
        type: String,
        streamId: Int,
        topicName: String,
        messageText: String
    ): Single<SendResponse> {
        return apiService.sendMessage(type, streamId, topicName, messageText)
    }

    fun addReaction(
        messageId: Int,
        emojiName: String
    ): Single<SendResponse> {
        return apiService.addReaction(messageId, emojiName)
    }

    fun removeReaction(
        messageId: Int,
        emojiName: String
    ): Single<SendResponse> {
        return apiService.removeReaction(messageId, emojiName)
    }

    companion object {
        private const val TAG = "NetworkManager"
    }
}
