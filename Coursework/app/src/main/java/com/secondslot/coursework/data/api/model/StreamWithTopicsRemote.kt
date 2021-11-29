package com.secondslot.coursework.data.api.model

import com.secondslot.coursework.domain.model.Stream

class StreamWithTopicsRemote(
    val streamRemote: StreamRemote,
    val topics: List<TopicRemote>
)

fun StreamWithTopicsRemote.toDomainModel(isSubscribed: Boolean): Stream = Stream(
    id = this.streamRemote.id,
    streamName = this.streamRemote.streamName,
    description = this.streamRemote.description ?: "",
    topics = this.topics.map { topicRemote ->
        Stream.Topic(
            topicName = topicRemote.topicName,
            maxMessageId = topicRemote.maxMessageId,
            streamId = this.streamRemote.id,
            isSubscribed = isSubscribed
        )
    }
)

object StreamTopicsRemoteToStreamMapper {

    fun map(type: List<StreamWithTopicsRemote>?, isSubscribed: Boolean): List<Stream> {
        return type?.map { it.toDomainModel(isSubscribed) } ?: emptyList()
    }
}
