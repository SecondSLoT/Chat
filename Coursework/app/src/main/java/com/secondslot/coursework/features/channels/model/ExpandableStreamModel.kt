package com.secondslot.coursework.features.channels.model

import com.secondslot.coursework.domain.model.Stream

class ExpandableStreamModel {

    var stream: Stream
    var type: Int
    lateinit var topic: Stream.Topic
    var isExpanded: Boolean

    constructor(
        type: Int,
        stream: Stream,
        isExpanded: Boolean = false
    ) {
        this.type = type
        this.stream = stream
        this.isExpanded = isExpanded
    }

    constructor(
        type: Int,
        topic: Stream.Topic,
        isExpanded: Boolean = false
    ) {
        this.stream = Stream(-1, "stub", "stub", emptyList())
        this.type = type
        this.topic = topic
        this.isExpanded = isExpanded
    }

    companion object {
        const val PARENT = 1
        const val CHILD = 2

        fun fromStream(streams: List<Stream>): List<ExpandableStreamModel> =
            streams.map {
                ExpandableStreamModel(
                    type = PARENT,
                    stream = it
                )
            }
    }
}
