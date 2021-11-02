package com.secondslot.coursework.data.db.model

import com.secondslot.coursework.domain.model.ChannelGroup

class ChannelGroupEntity(
    val id: Int,
    val groupTitle: String,
    val channels: List<ChannelEntity>
) {

    class ChannelEntity(
        val id: Int,
        val topic: String,
        val someMoreInfo: String
    )
}

fun ChannelGroupEntity.toDomainModel(): ChannelGroup = ChannelGroup(
    id = this.id,
    groupTitle = this.groupTitle,
    channels = this.channels.map {
        ChannelGroup.Channel(
            id = it.id,
            topic = it.topic,
            someMoreInfo = it.someMoreInfo
        )
    }
)

