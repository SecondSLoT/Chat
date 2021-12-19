package com.secondslot.coursework.navigation

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.secondslot.coursework.R
import com.secondslot.coursework.features.channels.ui.CreateStreamDialog
import com.secondslot.coursework.features.chat.ui.ChatFragment
import com.secondslot.coursework.features.chat.ui.EditMessageDialog
import com.secondslot.coursework.features.chat.ui.MoveMessageDialog
import com.secondslot.coursework.features.dialog.StandardAlertDialog
import com.secondslot.coursework.features.profile.ui.ProfileFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class Navigator @AssistedInject constructor(
    @Assisted private val activity: FragmentActivity
) : AppNavigation {

    override fun navigateToProfileFragment(userId: Int) {
        startFragment(ProfileFragment.newInstance(userId))
    }

    override fun navigateToChatFragment(topicName: String, maxMessageId: Int, streamId: Int) {
        startFragment(ChatFragment.newInstance(topicName, maxMessageId, streamId))
    }

    private fun startFragment(fragment: Fragment, tag: String? = null) {
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    override fun navigateToCreateStreamDialog(
        requestKey: String,
        streamNameKey: String,
        descriptionKey: String
    ) {
        val dialog = CreateStreamDialog.newInstance(requestKey, streamNameKey, descriptionKey)
        startDialog(dialog)
    }

    override fun navigateToStandardAlertDialog(
        requestKey: String,
        title: String,
        message: String,
        positiveButtonText: String,
        resultKey: String
    ) {
        val dialog = StandardAlertDialog.newInstance(
            requestKey,
            title,
            message,
            positiveButtonText,
            resultKey
        )
        startDialog(dialog)
    }

    override fun navigateToEditMessageDialog(
        requestKey: String,
        messageKey: String,
        oldMessageText: String,
        resultKey: String
    ) {
        val dialog = EditMessageDialog.newInstance(
            requestKey = requestKey,
            messageKey = messageKey,
            oldMessageText = oldMessageText,
            resultKey = resultKey
        )
        startDialog(dialog)
    }

    override fun navigateToMoveMessageDialog(
        requestKey: String,
        topics: List<String>,
        newTopicKey: String,
        resultKey: String
    ) {
        val dialog = MoveMessageDialog.newInstance(
            requestKey = requestKey,
            topics = topics,
            newTopicKey = newTopicKey,
            resultKey = resultKey
        )
        startDialog(dialog)
    }

    private fun startDialog(dialog: DialogFragment, tag: String? = null) {
        dialog.show(activity.supportFragmentManager, tag)
    }
}
