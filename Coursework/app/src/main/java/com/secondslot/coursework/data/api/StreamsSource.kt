package com.secondslot.coursework.data.api

import androidx.annotation.WorkerThread
import com.secondslot.coursework.domain.model.ChannelGroup
import com.secondslot.coursework.domain.model.ExpandableChannelModel

object StreamsSource {

    @WorkerThread
    fun getAllStreams(): List<ExpandableChannelModel> {
        return listOf(
            ExpandableChannelModel(
                ExpandableChannelModel.PARENT,
                ChannelGroup(
                    101,
                    groupTitle = "#channel_1",
                    listOf(ChannelGroup.Channel(105, "test_5", "100 mes"))
                )
            ),
            ExpandableChannelModel(
                ExpandableChannelModel.PARENT,
                ChannelGroup(
                    102,
                    groupTitle = "#channel_2",
                    listOf(
                        ChannelGroup.Channel(101, "test_5", "100 mes"),
                        ChannelGroup.Channel(102, "test_6", "200 mes")
                    )
                )
            ),
            ExpandableChannelModel(
                ExpandableChannelModel.PARENT,
                ChannelGroup(
                    103,
                    groupTitle = "#channel_3",
                    listOf(
                        ChannelGroup.Channel(103, "test_50", "300 mes"),
                        ChannelGroup.Channel(104, "test_60", "400 mes")
                    )
                )
            )
        )
    }
}
