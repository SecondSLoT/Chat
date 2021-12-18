package com.secondslot.coursework.navigation

interface AppNavigation {

    fun navigateToProfileFragment(userId: Int)

    fun navigateToChatFragment(topicName: String, maxMessageId: Int, streamId: Int)

    fun navigateToCreateStreamDialog(
        requestKey: String,
        streamNameKey: String,
        descriptionKey: String
    )

    fun navigateToStandardAlertDialog(
        requestKey: String,
        title: String,
        message: String,
        positiveButtonText: String,
        resultKey: String
    )

    fun navigateToEditMessageDialog(
        requestKey: String,
        messageKey: String,
        oldMessageText: String,
        resultKey: String
    )

    fun navigateToMoveMessageDialog(
        requestKey: String,
        topics: List<String>,
        newTopicKey: String,
        resultKey: String
    )
}
