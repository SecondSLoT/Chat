package com.secondslot.coursework.features.channels.model

import com.secondslot.coursework.domain.model.ChannelGroup

class ExpandableChannelModel {

    lateinit var channelGroup: ChannelGroup
    var type: Int
    lateinit var channel: ChannelGroup.Channel
    var isExpanded: Boolean
    private var isCloseShown: Boolean

    constructor(
        type: Int,
        channelGroup: ChannelGroup,
        isExpanded: Boolean = false,
        isCloseShown: Boolean = false
    ) {
        this.type = type
        this.channelGroup = channelGroup
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

    constructor(
        type: Int,
        channel: ChannelGroup.Channel,
        isExpanded: Boolean = false,
        isCloseShown: Boolean = false
    ) {
        this.type = type
        this.channel = channel
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

    companion object {
        const val PARENT = 1
        const val CHILD = 2

        fun fromChannelGroup(channelGroups: List<ChannelGroup>): List<ExpandableChannelModel> =
            channelGroups.map {
                ExpandableChannelModel(
                    type = ExpandableChannelModel.PARENT,
                    channelGroup = it
                )
            }
    }
}
