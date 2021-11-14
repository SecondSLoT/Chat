package com.secondslot.coursework.data.db.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.secondslot.coursework.domain.model.Stream

@Entity(tableName = "streams")
class StreamEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "stream_name") val streamName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "is_subscribed") val isSubscribed: Boolean
) {

    companion object {
        fun fromDomainModel(
            stream: Stream,
            isSubscribed: Boolean = false
        ): StreamEntity = StreamEntity(
            id = stream.id,
            streamName = stream.streamName,
            description = stream.description,
            isSubscribed = isSubscribed
        )
    }
}


