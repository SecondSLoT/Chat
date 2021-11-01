package com.secondslot.coursework.domain.model

data class ChannelGroup(
    val id: Int,
    val groupTitle: String,
    val channels: List<Channel>
) {

    data class Channel(
        val id: Int,
        val channelTitle: String,
        val someMoreInfo: String
    )
}

