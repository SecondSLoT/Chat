package com.secondslot.coursework.data.db.model

import com.secondslot.coursework.domain.model.Stream

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

//fun ChannelGroupEntity.toDomainModel(): Stream = Stream(
//    id = this.id,
//    streamName = this.groupTitle,
//    topics = this.channels.map {
//        Stream.Topic(
//            id = it.id,
//            topicName = it.topic,
//            someMoreInfo = it.someMoreInfo
//        )
//    }
//)

