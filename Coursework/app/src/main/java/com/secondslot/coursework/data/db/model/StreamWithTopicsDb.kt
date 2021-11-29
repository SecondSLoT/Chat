package com.secondslot.coursework.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.secondslot.coursework.base.mapper.BaseMapper
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

fun StreamWithTopicsDb.toDomainModel(): Stream = Stream(
    id = this.streamEntity.id,
    streamName = this.streamEntity.streamName,
    description = this.streamEntity.description,
    topics = this.topics.map { topicEntity ->
        Stream.Topic(
            topicName = topicEntity.topicName,
            maxMessageId = topicEntity.maxMessageId,
            streamId = topicEntity.streamId,
            isSubscribed = topicEntity.isSubscribed
        )
    }
)

object StreamWithTopicsDbToDomainMapper : BaseMapper<List<StreamWithTopicsDb>, List<Stream>> {

    override fun map(type: List<StreamWithTopicsDb>?): List<Stream> {
        return type?.map { it.toDomainModel() } ?: emptyList()
    }
}
