package com.secondslot.coursework.data.db.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.secondslot.coursework.core.mapper.BaseMapper
import com.secondslot.coursework.domain.model.Stream

@Entity(
    tableName = "topics",
    foreignKeys = [ForeignKey(
        entity = StreamEntity::class,
        parentColumns = ["id", "is_subscribed"],
        childColumns = ["stream_id", "is_subscribed"],
        onDelete = CASCADE
    )]
)
class TopicEntity(
    @PrimaryKey
    @ColumnInfo(name = "topic_name") val topicName: String,
    @ColumnInfo(name = "max_message_id") val maxMessageId: Int,
    @ColumnInfo(name = "stream_id") val streamId: Int, // Foreign key part
    @ColumnInfo(name = "is_subscribed") val isSubscribed: Boolean // Foreign key part
)

object TopicToTopicEntityMapper : BaseMapper<List<Stream.Topic>, List<TopicEntity>> {

    override fun map(type: List<Stream.Topic>?): List<TopicEntity> {
        return type?.map {
            TopicEntity(
                topicName = it.topicName,
                maxMessageId = it.maxMessageId,
                streamId = it.streamId,
                isSubscribed = it.isSubscribed
            )
        } ?: emptyList()
    }
}
