package com.secondslot.coursework.features.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.databinding.ItemDateDividerBinding
import com.secondslot.coursework.databinding.ItemMessageBinding
import com.secondslot.coursework.features.chat.listener.MessageInteractionListener
import com.secondslot.coursework.features.chat.model.ChatItem
import com.secondslot.coursework.features.chat.model.DateDivider
import com.secondslot.coursework.features.chat.model.MessageItem
import com.secondslot.coursework.features.chat.ui.DateViewHolder
import com.secondslot.coursework.features.chat.ui.MessageViewHolder

class ChatAdapter(
    private val listener: MessageInteractionListener,
    private val myId: Int
) : ListAdapter<ChatItem, RecyclerView.ViewHolder>(MessageComparator()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MessageItem -> ItemType.MESSAGE.type
            else -> ItemType.DATE.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ItemType.MESSAGE.type) {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                ItemMessageBinding.inflate(layoutInflater, parent, false)
            MessageViewHolder(binding, listener)
        } else {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                ItemDateDividerBinding.inflate(layoutInflater, parent, false)
            DateViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageViewHolder -> holder.bind(getItem(position) as MessageItem, myId)
            is DateViewHolder -> holder.bind(getItem(position) as DateDivider)
        }
    }
}

private enum class ItemType(val type: Int) {
    MESSAGE(0),
    DATE(1)
}

class MessageComparator : DiffUtil.ItemCallback<ChatItem>() {

    override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
        return when (oldItem) {
            is MessageItem -> {
                if (newItem is MessageItem) oldItem.id == newItem.id else false
            }

            is DateDivider -> {
                if (newItem is DateDivider) oldItem.date == newItem.date else false
            }

            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
        return when (oldItem) {
            is MessageItem -> {
                if (newItem is MessageItem) {
                    oldItem as MessageItem == newItem as MessageItem
                } else {
                    false
                }
            }

            is DateDivider -> {
                if (newItem is DateDivider) oldItem.date == newItem.date else false
            }

            else -> false
        }
    }

    override fun getChangePayload(oldItem: ChatItem, newItem: ChatItem) = Any()
}
