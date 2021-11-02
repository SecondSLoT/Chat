package com.secondslot.coursework.data.api

import androidx.annotation.WorkerThread
import com.secondslot.coursework.data.db.model.ChannelGroupEntity

object StreamsSource {

    @WorkerThread
    fun getAllStreams(): List<ChannelGroupEntity> {
        return listOf(
            ChannelGroupEntity(
                101,
                groupTitle = "#channel_1",
                listOf(
                    ChannelGroupEntity.ChannelEntity(
                        105,
                        "test_5",
                        "100 mes"
                    )
                )
            ),
            ChannelGroupEntity(
                102,
                groupTitle = "#channel_2",
                listOf(
                    ChannelGroupEntity.ChannelEntity(
                        101,
                        "test_5",
                        "100 mes"
                    ),
                    ChannelGroupEntity.ChannelEntity(
                        102,
                        "test_6",
                        "200 mes"
                    )
                )
            ),
            ChannelGroupEntity(
                103,
                groupTitle = "#channel_3",
                listOf(
                    ChannelGroupEntity.ChannelEntity(
                        103,
                        "test_50",
                        "300 mes"
                    ),
                    ChannelGroupEntity.ChannelEntity(
                        104,
                        "test_60",
                        "400 mes"
                    )
                )
            )
        )
    }
}
