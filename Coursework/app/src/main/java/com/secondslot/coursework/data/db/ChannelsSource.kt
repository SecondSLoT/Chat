package com.secondslot.coursework.data.db

import androidx.annotation.WorkerThread
import com.secondslot.coursework.domain.model.ChannelGroup
import com.secondslot.coursework.domain.model.ExpandableChannelModel

object ChannelsSource {

    @WorkerThread
    fun getChannels(): List<ExpandableChannelModel> {
        return listOf(
            ExpandableChannelModel(
                ExpandableChannelModel.PARENT,
                ChannelGroup(
                    id = 1,
                    groupTitle = "#general",
                    listOf(
                        ChannelGroup.Channel(1, "Testing", "1240 mes"),
                        ChannelGroup.Channel(2, "Bruh", "24 mes")
                    )
                )
            ),
            ExpandableChannelModel(
                ExpandableChannelModel.PARENT,
                ChannelGroup(
                    id = 2,
                    groupTitle = "#Development",
                    listOf(
                        ChannelGroup.Channel(3, "test_1", "100 mes"),
                        ChannelGroup.Channel(4, "test_2", "200 mes")
                    )
                )
            ),
            ExpandableChannelModel(
                ExpandableChannelModel.PARENT,
                ChannelGroup(
                    id = 3,
                    groupTitle = "#Design",
                    listOf(
                        ChannelGroup.Channel(5, "test_10", "300 mes"),
                        ChannelGroup.Channel(6, "test_20", "400 mes")
                    )
                )
            ),
            ExpandableChannelModel(
                ExpandableChannelModel.PARENT,
                ChannelGroup(
                    id = 4,
                    groupTitle = "#PR",
                    listOf(
                        ChannelGroup.Channel(7, "test_100", "500 mes"),
                        ChannelGroup.Channel(8, "test_200", "600 mes"),
                        ChannelGroup.Channel(9, "test_300", "700 mes")
                    )
                )
            )
        )
    }
}
