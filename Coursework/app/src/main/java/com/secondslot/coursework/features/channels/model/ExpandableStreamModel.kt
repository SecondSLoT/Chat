package com.secondslot.coursework.features.channels.model

import com.secondslot.coursework.domain.model.Stream

class ExpandableStreamModel {

    lateinit var stream: Stream
    var type: Int
    lateinit var topic: Stream.Topic
    var isExpanded: Boolean
    private var isCloseShown: Boolean

    constructor(
        type: Int,
        channelGroup: Stream,
        isExpanded: Boolean = false,
        isCloseShown: Boolean = false
    ) {
        this.type = type
        this.stream = channelGroup
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

    constructor(
        type: Int,
        stream: Stream.Topic,
        isExpanded: Boolean = false,
        isCloseShown: Boolean = false
    ) {
        this.type = type
        this.topic = stream
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

    companion object {
        const val PARENT = 1
        const val CHILD = 2

        fun fromStream(streams: List<Stream>): List<ExpandableStreamModel> =
            streams.map {
                ExpandableStreamModel(
                    type = PARENT,
                    channelGroup = it
                )
            }
    }
}
