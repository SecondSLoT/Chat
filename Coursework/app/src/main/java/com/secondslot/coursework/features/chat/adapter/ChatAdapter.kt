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
import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.model.DateDivider
import com.secondslot.coursework.features.chat.model.MessageItem
import com.secondslot.coursework.features.chat.ui.DateViewHolder
import com.secondslot.coursework.features.chat.ui.MessageInteractionListener
import com.secondslot.coursework.features.chat.ui.MessageViewHolder

private const val TYPE_MESSAGE = 0
private const val TYPE_DATE = 1

class ChatAdapter(
    private val listener: MessageInteractionListener,
    private val myId: Int
) : ListAdapter<ChatItem, RecyclerView.ViewHolder>(MessageComparator()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MessageItem -> TYPE_MESSAGE
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
                    listener.openReactionsSheet(
                        getItem(holder.bindingAdapterPosition) as MessageItem
                    )
                    true
                }

                // Set OnClickListener on Add reaction button
                val addReactionButton =
                    messageViewGroup.findViewById<ImageButton>(R.id.add_reaction_button)
                addReactionButton.setOnClickListener {
                    listener.openReactionsSheet(
                        getItem(holder.bindingAdapterPosition) as MessageItem
                    )
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
            holder.bind(getItem(position) as MessageItem, myId)
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
            is MessageItem -> {
                if (newItem is MessageItem) {
                    oldItem.id == newItem.id &&
                        oldItem.senderId == newItem.senderId &&
                        oldItem.senderFullName == newItem.senderFullName &&
                        oldItem.content == newItem.content &&
                        oldItem.topic == newItem.topic &&
                        oldItem.reactions == newItem.reactions
                } else {
                    false
                }
            }

            is DateDivider -> {
                if (newItem is DateDivider) {
                    oldItem.date == newItem.date
                } else {
                    false
                }
            }

            else -> false
        }
    }

    override fun getChangePayload(oldItem: ChatItem, newItem: ChatItem) = Any()
}
