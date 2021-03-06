package com.secondslot.coursework.data.api.model

import com.secondslot.coursework.domain.model.Stream

class StreamWithTopicsRemote(
    val streamRemote: StreamRemote,
    val topics: List<TopicRemote>
)

object StreamTopicsRemoteToStreamMapper {

    fun map(type: List<StreamWithTopicsRemote>?, isSubscribed: Boolean): List<Stream> {
        return type?.map {
            Stream(
                id = it.streamRemote.id,
                streamName = it.streamRemote.streamName,
                description = it.streamRemote.description ?: "",
                topics = it.topics.map { topicRemote ->
                    Stream.Topic(
                        topicName = topicRemote.topicName,
                        maxMessageId = topicRemote.maxMessageId,
                        streamId = it.streamRemote.id,
                        isSubscribed = isSubscribed
                    )
                }
            )
        } ?: emptyList()
    }
}
