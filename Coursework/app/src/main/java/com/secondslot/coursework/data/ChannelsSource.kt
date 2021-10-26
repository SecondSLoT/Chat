package com.secondslot.coursework.data

import com.secondslot.coursework.domain.model.ExpandableChannel
import com.secondslot.coursework.domain.model.ExpandableChannelModel

object ChannelsSource {

    val subscribed = mutableListOf(
        ExpandableChannelModel(
            ExpandableChannelModel.PARENT,
            ExpandableChannel.ChannelGroup(
                group = "#general",
                listOf(
                    ExpandableChannel.ChannelGroup.Channel("Testing", "1240 mes"),
                    ExpandableChannel.ChannelGroup.Channel("Bruh", "24 mes")
                )
            )
        ),
        ExpandableChannelModel(
            ExpandableChannelModel.PARENT,
            ExpandableChannel.ChannelGroup(
                group = "#Development",
                listOf(
                    ExpandableChannel.ChannelGroup.Channel("test_1", "100 mes"),
                    ExpandableChannel.ChannelGroup.Channel("test_2", "200 mes")
                )
            )
        ),
        ExpandableChannelModel(
            ExpandableChannelModel.PARENT,
            ExpandableChannel.ChannelGroup(
                group = "#Design",
                listOf(
                    ExpandableChannel.ChannelGroup.Channel("test_10", "300 mes"),
                    ExpandableChannel.ChannelGroup.Channel("test_20", "400 mes")
                )
            )
        ),
        ExpandableChannelModel(
            ExpandableChannelModel.PARENT,
            ExpandableChannel.ChannelGroup(
                group = "#PR",
                listOf(
                    ExpandableChannel.ChannelGroup.Channel("test_100", "500 mes"),
                    ExpandableChannel.ChannelGroup.Channel("test_200", "600 mes"),
                    ExpandableChannel.ChannelGroup.Channel("test_300", "700 mes")
                )
            )
        )
    )

    val allStreams = mutableListOf(
        ExpandableChannelModel(
            ExpandableChannelModel.PARENT,
            ExpandableChannel.ChannelGroup(
                group = "#channel_1",
                listOf()
            )
        ),
        ExpandableChannelModel(
            ExpandableChannelModel.PARENT,
            ExpandableChannel.ChannelGroup(
                group = "#channel_2",
                listOf(
                    ExpandableChannel.ChannelGroup.Channel("test_5", "100 mes"),
                    ExpandableChannel.ChannelGroup.Channel("test_6", "200 mes")
                )
            )
        ),
        ExpandableChannelModel(
            ExpandableChannelModel.PARENT,
            ExpandableChannel.ChannelGroup(
                group = "#channel_3",
                listOf(
                    ExpandableChannel.ChannelGroup.Channel("test_50", "300 mes"),
                    ExpandableChannel.ChannelGroup.Channel("test_60", "400 mes")
                )
            )
        )
    )
}
