package com.secondslot.coursework.features.chat.ui

import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.widget.ImageButton
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.R
import com.secondslot.coursework.customview.CustomFlexBoxLayout
import com.secondslot.coursework.customview.CustomReactionView
import com.secondslot.coursework.databinding.ItemMessageBinding
import com.secondslot.coursework.extentions.fromHtml
import com.secondslot.coursework.features.chat.listener.MessageInteractionListener
import com.secondslot.coursework.features.chat.model.MessageItem

class MessageViewHolder(
    private val binding: ItemMessageBinding,
    private val listener: MessageInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    private var item: MessageItem? = null

    init {
        // Open bottom sheet on long click on message
        binding.messageViewGroup.setOnLongClickListener {
            item?.let { listener.messageOnLongClick(it) }
            true
        }

        // Set OnClickListener on Add reaction button
        val addReactionButton =
            binding.messageViewGroup.findViewById<ImageButton>(R.id.add_reaction_button)
        addReactionButton.setOnClickListener {
            item?.let { listener.onAddReactionButtonClick(it) }
        }
    }


    fun bind(message: MessageItem, myId: Int) {
        item = message
        binding.messageViewGroup.run {
            message.avatarUrl?.let { setUserPhoto(it) }
            message.senderFullName?.let { setUsername(it) }
            setMessageText(message.content.fromHtml())
        }

        val customFlexBoxLayout =
            binding.messageViewGroup
                .findViewById<CustomFlexBoxLayout>(R.id.custom_flex_box_layout)
        customFlexBoxLayout.removeViews(0, customFlexBoxLayout.childCount - 1)

        if (message.reactions.isNotEmpty()) {
            customFlexBoxLayout.isGone = false

            val index = customFlexBoxLayout.childCount - 1
            for (reaction in message.reactions) {
                val reactionView = CustomReactionView(itemView.context)
                reactionView.run {
                    emoji = reaction.key
                    counter = reaction.value.size
                    isSelected = reaction.value.find { it.userId == myId} != null

                    setOnClickListener {
                        it as CustomReactionView
                        if (it.isSelected) {
                            listener.removeReaction(message.id, reaction.value[0].emojiName)
                        } else {
                            listener.addReaction(message.id, reaction.value[0].emojiName)
                        }
                    }
                }
                customFlexBoxLayout.addView(reactionView, index)
            }
        } else {
            customFlexBoxLayout.isGone = true
        }

        if (message.senderId == myId) {
            binding.messageViewGroup.setMessageBgColor(R.color.own_message_background)
            binding.messageViewGroup.setSelfMessageType(true)
        } else {
            binding.messageViewGroup.setMessageBgColor(R.color.on_background)
            binding.messageViewGroup.setSelfMessageType(false)
        }
    }
}
