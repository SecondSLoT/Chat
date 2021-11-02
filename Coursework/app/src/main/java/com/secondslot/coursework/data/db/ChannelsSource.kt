package com.secondslot.coursework.data.db

import androidx.annotation.WorkerThread
import com.secondslot.coursework.data.db.model.ChannelGroupEntity

object ChannelsSource {

    @WorkerThread
    fun getChannels(): List<ChannelGroupEntity> {
        return listOf(
            ChannelGroupEntity(
                id = 1,
                groupTitle = "#general",
                listOf(
                    ChannelGroupEntity.ChannelEntity(1, "Testing", "1240 mes"),
                    ChannelGroupEntity.ChannelEntity(2, "Bruh", "24 mes")
                )
            ),

            ChannelGroupEntity(
                id = 2,
                groupTitle = "#Development",
                listOf(
                    ChannelGroupEntity.ChannelEntity(3, "test_1", "100 mes"),
                    ChannelGroupEntity.ChannelEntity(4, "test_2", "200 mes")
                )

            ),

            ChannelGroupEntity(
                id = 3,
                groupTitle = "#Design",
                listOf(
                    ChannelGroupEntity.ChannelEntity(5, "test_10", "300 mes"),
                    ChannelGroupEntity.ChannelEntity(6, "test_20", "400 mes")
                )

            ),

            ChannelGroupEntity(
                id = 4,
                groupTitle = "#PR",
                listOf(
                    ChannelGroupEntity.ChannelEntity(7, "test_100", "500 mes"),
                    ChannelGroupEntity.ChannelEntity(8, "test_200", "600 mes"),
                    ChannelGroupEntity.ChannelEntity(9, "test_300", "700 mes")
                )
            )
        )
    }
}
