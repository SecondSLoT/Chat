package com.secondslot.coursework.data.db.model

class ChannelGroupEntity(
    val id: Int,
    val group: String,
    val channels: List<ChannelEntity>
) {

    class ChannelEntity(
        val id: Int,
        val channelTitle: String,
        val someMoreInfo: String
    )
}

