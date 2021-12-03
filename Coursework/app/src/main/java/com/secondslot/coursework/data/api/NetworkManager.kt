package com.secondslot.coursework.data.api

import com.secondslot.coursework.data.api.model.MessageRemote
import com.secondslot.coursework.data.api.model.StreamRemote
import com.secondslot.coursework.data.api.model.StreamWithTopicsRemote
import com.secondslot.coursework.data.api.model.UserRemote
import com.secondslot.coursework.data.api.model.response.SendResponse
import javax.inject.Inject

class NetworkManager @Inject constructor(
    private val apiService: ZulipApiService
) {

    suspend fun getSubscribedStreams(): List<StreamWithTopicsRemote> {
        return mapStreams(apiService.getSubscribedStreams().streams)
    }

    suspend fun getAllStreams(): List<StreamWithTopicsRemote> {
        return mapStreams(apiService.getAllStreams().streams)
    }

    private suspend fun mapStreams(streamRemoteList: List<StreamRemote>):
        List<StreamWithTopicsRemote> {
        return streamRemoteList
            .map { streamRemote ->
                val topics = apiService.getTopics(streamRemote.id).topics
                StreamWithTopicsRemote(streamRemote, topics)
            }
    }

    suspend fun getAllUsers(): List<UserRemote> =
        apiService.getAllUsers().users.sortedBy { user -> user.fullName }

    suspend fun getUser(userId: Int): List<UserRemote> =
        listOf(apiService.getUser(userId).user)

    suspend fun getOwnUser(): List<UserRemote> = listOf(apiService.getOwnUser())

    suspend fun getMessages(
        anchor: String,
        numBefore: String,
        numAfter: String,
        narrow: Map<String, Any>
    ): List<MessageRemote> {
        val narrowValue = generateNarrowValue(narrow)
        return apiService.getMessages(anchor, numBefore, numAfter, narrowValue).messages
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

    suspend fun sendMessage(
        type: String,
        streamId: Int,
        topicName: String,
        messageText: String
    ): SendResponse {
        return apiService.sendMessage(type, streamId, topicName, messageText)
    }

    suspend fun addReaction(
        messageId: Int,
        emojiName: String
    ): SendResponse {
        return apiService.addReaction(messageId, emojiName)
    }

    suspend fun removeReaction(
        messageId: Int,
        emojiName: String
    ): SendResponse {
        return apiService.removeReaction(messageId, emojiName)
    }

    companion object {
        private const val TAG = "NetworkManager"
    }
}
