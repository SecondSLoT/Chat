package com.secondslot.coursework.data.api.model

import com.secondslot.coursework.core.mapper.BaseMapper
import com.secondslot.coursework.domain.model.Stream

class StreamWithTopicsRemote(
    val streamRemote: StreamRemote,
    val topics: List<TopicRemote>
)

object RemoteToDomainMapper : BaseMapper<List<StreamWithTopicsRemote>, List<Stream>> {

    override fun map(type: List<StreamWithTopicsRemote>?): List<Stream> {
        return type?.map {
            Stream(
                id = it.streamRemote.id,
                streamName = it.streamRemote.streamName,
                description = it.streamRemote.description ?: "",
                color = it.streamRemote.color ?: "",
                role = it.streamRemote.role,
                topics = it.topics.map { topicRemote ->
                    Stream.Topic(
                        id = topicRemote.id,
                        topicName = topicRemote.topicName
                    )
                }
            )
        } ?: emptyList()
    }
}
