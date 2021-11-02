package com.secondslot.coursework.features.channels.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.R
import com.secondslot.coursework.features.channels.model.ExpandableChannelModel
import com.secondslot.coursework.features.channels.ui.ExpandCollapseListener
import com.secondslot.coursework.features.channels.ui.OnChatClickListener

class ChannelsListAdapter(
    private val expandCollapseListener: ExpandCollapseListener,
    private val chatListener: OnChatClickListener
) : ListAdapter<ExpandableChannelModel, RecyclerView.ViewHolder>(ChannelsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ExpandableChannelModel.PARENT -> {
                ChannelGroupViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_channel_group, parent, false
                    )
                )
            }

            else -> {
                ChannelViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_channel, parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = getItem(holder.absoluteAdapterPosition)
        when (row.type) {
            ExpandableChannelModel.PARENT -> {

                (holder as ChannelGroupViewHolder).groupTitle.text =
                    row.channelGroup.groupTitle

                // Set initial holder state to get rid of dirty holders because of reusing
                holder.collapseArrow.visibility = View.GONE
                holder.expandArrow.visibility = View.VISIBLE

                holder.expandArrow.setOnClickListener {
                    row.isExpanded = true
                    holder.collapseArrow.visibility = View.VISIBLE
                    holder.expandArrow.visibility = View.GONE
                    expandCollapseListener.expandRow(holder.absoluteAdapterPosition)
                }

                holder.collapseArrow.setOnClickListener {
                    if (row.isExpanded) {
                        row.isExpanded = false
                        expandCollapseListener.collapseRow(holder.absoluteAdapterPosition)
                        holder.collapseArrow.visibility = View.GONE
                        holder.expandArrow.visibility = View.VISIBLE

                    }
                }
            }

            ExpandableChannelModel.CHILD -> {
                (holder as ChannelViewHolder).channel.text = row.channel.topic
                holder.additionalInfo.text = row.channel.someMoreInfo

                holder.itemView.setOnClickListener {
                    val channel = getItem(holder.absoluteAdapterPosition).channel
                    chatListener.onChannelClicked(
                        channel.id,
                        channel.topic
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type


    class ChannelGroupViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        internal var groupTitle: TextView = itemView.findViewById(R.id.title_text_view)
        internal var expandArrow: ImageView = itemView.findViewById(R.id.expand_arrow)
        internal var collapseArrow: ImageView = itemView.findViewById(R.id.collapse_arrow)
    }

    class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var channel: TextView = itemView.findViewById(R.id.channel_text_view)
        internal var additionalInfo: TextView = itemView.findViewById(R.id.additional_info)
    }
}

class ChannelsComparator : DiffUtil.ItemCallback<ExpandableChannelModel>() {

    override fun areItemsTheSame(
        oldItem: ExpandableChannelModel,
        newItem: ExpandableChannelModel
    ): Boolean {
        if (oldItem.type == ExpandableChannelModel.PARENT &&
            newItem.type == ExpandableChannelModel.PARENT
        ) {
            return oldItem.channelGroup.id == newItem.channelGroup.id

        } else if (oldItem.type == ExpandableChannelModel.CHILD &&
            newItem.type == ExpandableChannelModel.CHILD
        ) {
            return oldItem.channel.id == newItem.channel.id
        }
        return false
    }

    override fun areContentsTheSame(
        oldItem: ExpandableChannelModel,
        newItem: ExpandableChannelModel
    ): Boolean {
        if (oldItem.type == ExpandableChannelModel.PARENT &&
            newItem.type == ExpandableChannelModel.PARENT
        ) {
            return oldItem.channelGroup == newItem.channelGroup &&
                oldItem.isExpanded == newItem.isExpanded

        } else if (oldItem.type == ExpandableChannelModel.CHILD &&
            newItem.type == ExpandableChannelModel.CHILD
        ) {
            return oldItem.channel == newItem.channel
        }
        return false
    }
}
