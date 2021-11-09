package com.secondslot.coursework.features.channels.ui

interface OnTopicClickListener {

    fun onTopicClicked(topicName: String, maxMessageId: Int, streamId: Int)
}
