package com.secondslot.coursework.features.chatscreen.ui

import android.widget.ImageView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.R
import com.secondslot.coursework.customview.CustomFlexBoxLayout
import com.secondslot.coursework.customview.CustomReactionView
import com.secondslot.coursework.databinding.ItemMessageBinding
import com.secondslot.coursework.domain.model.Message

class MessageViewHolder(
    private val binding: ItemMessageBinding,
    private val listener: MessageInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message) {
        binding.messageViewGroup.run {
            setUsername(message.username)
            setMessageText(message.message)
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
                reactionView.emoji = reaction.code
                reactionView.counter = reaction.count
                reactionView.isSelected = reaction.isSelected
                reactionView.setOnClickListener {
                    it as CustomReactionView
                    if (it.isSelected) {
                        it.isSelected = false
                        it.counter--
                    } else {
                        it.isSelected = true
                        it.counter++
                    }
                    if (it.counter == 0) {
                        message.reactions.remove(reaction)
                        listener.removeReaction(message, reaction)
                        customFlexBoxLayout.removeView(it)
                    }
                }
                customFlexBoxLayout.addView(reactionView, index)
            }
        } else {
            customFlexBoxLayout.isGone = true
        }

        val userPhoto = binding.messageViewGroup.findViewById<ImageView>(R.id.user_photo)

        if (message.userId == 0L) {
            binding.messageViewGroup.setMessageBgColor(R.color.username)
            binding.messageViewGroup.setSelfMessageType(true)
        } else {
            binding.messageViewGroup.setMessageBgColor(R.color.on_background)
            binding.messageViewGroup.setSelfMessageType(false)
        }
    }
}
