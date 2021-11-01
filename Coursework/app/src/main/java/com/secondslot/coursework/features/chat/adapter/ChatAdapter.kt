package com.secondslot.coursework.features.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.ItemDateDividerBinding
import com.secondslot.coursework.databinding.ItemMessageBinding
import com.secondslot.coursework.domain.model.ChatItem
import com.secondslot.coursework.domain.model.DateDivider
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.features.chat.ui.DateViewHolder
import com.secondslot.coursework.features.chat.ui.MessageInteractionListener
import com.secondslot.coursework.features.chat.ui.MessageViewHolder

private const val TYPE_MESSAGE = 0
private const val TYPE_DATE = 1

class ChatAdapter(
    private val listener: MessageInteractionListener
) : ListAdapter<ChatItem, RecyclerView.ViewHolder>(MessageComparator()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Message -> TYPE_MESSAGE
            else -> TYPE_DATE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_MESSAGE) {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                ItemMessageBinding.inflate(layoutInflater, parent, false)
            val holder = MessageViewHolder(binding, listener)

            binding.run {
                // Open bottom sheet on long click on message
                messageViewGroup.setOnLongClickListener {
                    listener.openReactionsSheet(getItem(holder.bindingAdapterPosition) as Message)
                    true
                }

                // Set OnClickListener on Add reaction button
                val addReactionButton =
                    messageViewGroup.findViewById<ImageButton>(R.id.add_reaction_button)
                addReactionButton.setOnClickListener {
                    listener.openReactionsSheet(getItem(holder.bindingAdapterPosition) as Message)
                }
            }

            return holder
        } else {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                ItemDateDividerBinding.inflate(layoutInflater, parent, false)
            return DateViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MessageViewHolder) {
            holder.bind(getItem(position) as Message)
        } else {
            (holder as DateViewHolder).bind(getItem(position) as DateDivider)
        }
    }
}

class MessageComparator : DiffUtil.ItemCallback<ChatItem>() {

    override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
        return when (oldItem) {
            is Message -> {
                if (newItem is Message) {
                    oldItem.datetime == newItem.datetime &&
                        oldItem.username == newItem.username &&
                        oldItem.message == newItem.message &&
                        oldItem.reactions == newItem.reactions
                } else {
                    false
                }
            }

            is DateDivider -> {
                if (newItem is DateDivider) {
                    oldItem.date == (newItem as DateDivider).date
                } else {
                    false
                }
            }

            else -> false
        }
    }

    override fun getChangePayload(oldItem: ChatItem, newItem: ChatItem) = Any()
}
