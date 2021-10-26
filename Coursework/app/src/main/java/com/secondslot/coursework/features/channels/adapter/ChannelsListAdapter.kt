package com.secondslot.coursework.features.channels.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.R
import com.secondslot.coursework.domain.model.ExpandableChannelModel

class ChannelsListAdapter(
    var channelGroupList: MutableList<ExpandableChannelModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isFirstItemExpanded: Boolean = true
    private var actionLock = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ExpandableChannelModel.PARENT -> {
                ChannelGroupViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_chanel_group, parent, false
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
        val row = channelGroupList[position]
        when (row.type) {
            ExpandableChannelModel.PARENT -> {

                (holder as ChannelGroupViewHolder).groupTitle.text =
                    row.channelGroup.group

                holder.expandArrow.setOnClickListener {
                    if (row.isExpanded) {
                        row.isExpanded = false
                        collapseRow(position)

                    } else {
                        row.isExpanded = true
                        holder.collapseArrow.visibility = View.VISIBLE
                        holder.expandArrow.visibility = View.GONE
                        expandRow(position)
                    }
                }

                holder.collapseArrow.setOnClickListener {
                    if (row.isExpanded) {
                        row.isExpanded = false
                        collapseRow(position)
                        holder.collapseArrow.visibility = View.GONE
                        holder.expandArrow.visibility = View.VISIBLE

                    }
                }
            }

            ExpandableChannelModel.CHILD -> {
                (holder as ChannelViewHolder).channel.text = row.channel.channelTitle
                holder.additionalInfo.text = row.channel.someMoreInfo
            }
        }
    }

    override fun getItemCount(): Int = channelGroupList.size

    override fun getItemViewType(position: Int): Int = channelGroupList[position].type

    private fun expandRow(position: Int) {
        val row = channelGroupList[position]
        var nextPosition = position
        when (row.type) {
            ExpandableChannelModel.PARENT -> {
                for (child in row.channelGroup.channels) {
                    channelGroupList.add(
                        ++nextPosition,
                        ExpandableChannelModel(ExpandableChannelModel.CHILD, child)
                    )
                }
                notifyDataSetChanged()
            }
            ExpandableChannelModel.CHILD -> {
                notifyDataSetChanged()
            }
        }
    }

    private fun collapseRow(position: Int) {
        val row = channelGroupList[position]
        val nextPosition = position + 1

        when (row.type) {
            ExpandableChannelModel.PARENT -> {

                outerloop@ while (true) {
                    if (nextPosition == channelGroupList.size ||
                        channelGroupList[nextPosition].type == ExpandableChannelModel.PARENT) {
                        break@outerloop
                    }

                    channelGroupList.removeAt(nextPosition)
                }

                notifyDataSetChanged()
            }
        }
    }

    class ChannelGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var groupTitle: TextView = itemView.findViewById(R.id.title_text_view)
        internal var expandArrow: ImageView = itemView.findViewById(R.id.expand_arrow)
        internal var collapseArrow: ImageView = itemView.findViewById(R.id.collapse_arrow)
    }

    class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var channel: TextView = itemView.findViewById(R.id.channel_text_view)
        internal var additionalInfo: TextView = itemView.findViewById(R.id.additional_info)
    }
}
