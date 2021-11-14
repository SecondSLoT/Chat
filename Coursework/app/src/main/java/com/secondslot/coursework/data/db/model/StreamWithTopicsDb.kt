package com.secondslot.coursework.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.secondslot.coursework.core.mapper.BaseMapper
import com.secondslot.coursework.data.db.model.entity.StreamEntity
import com.secondslot.coursework.data.db.model.entity.TopicEntity
import com.secondslot.coursework.domain.model.Stream

class StreamWithTopicsDb(
    @Embedded val streamEntity: StreamEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "stream_id"
    )
    val topics: List<TopicEntity>
)

object StreamWithTopicsDbToDomainMapper : BaseMapper<List<StreamWithTopicsDb>, List<Stream>> {

    override fun map(type: List<StreamWithTopicsDb>?): List<Stream> {
        return type?.map {
            Stream(
                id = it.streamEntity.id,
                streamName = it.streamEntity.streamName,
                description = it.streamEntity.description,
                topics = it.topics.map { topicEntity ->
                    Stream.Topic(
                        topicName = topicEntity.topicName,
                        maxMessageId = topicEntity.maxMessageId,
                        streamId = topicEntity.streamId
                    )
                }
            )
        } ?: emptyList()
    }
}

object DomaintoStreamWithTopicsMapper
