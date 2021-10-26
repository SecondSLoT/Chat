package com.secondslot.coursework.domain.model

class ExpandableChannel(
    val groups: List<ChannelGroup>
) {

    class ChannelGroup(
        val group: String,
        val channels: List<Channel>
    ) {

        class Channel(
            val channelTitle: String,
            val someMoreInfo: String
        )
    }
}
