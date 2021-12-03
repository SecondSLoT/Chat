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
import com.secondslot.coursework.features.channels.model.ExpandableStreamModel
import com.secondslot.coursework.features.channels.ui.ExpandCollapseListener
import com.secondslot.coursework.features.channels.ui.OnTopicClickListener

class StreamsListAdapter(
    private val expandCollapseListener: ExpandCollapseListener,
    private val topicListener: OnTopicClickListener
) : ListAdapter<ExpandableStreamModel, RecyclerView.ViewHolder>(ChannelsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ExpandableStreamModel.PARENT -> {
                StreamViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_stream, parent, false
                    )
                )
            }

            else -> {
                TopicViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_topic, parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = getItem(holder.absoluteAdapterPosition)
        when (row.type) {
            ExpandableStreamModel.PARENT -> {

                val groupTitle = "#${row.stream.streamName}"
                (holder as StreamViewHolder).groupTitle.text = groupTitle

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

            ExpandableStreamModel.CHILD -> {
                (holder as TopicViewHolder).topic.text = row.topic.topicName

                holder.itemView.setOnClickListener {
                    val topic = getItem(holder.absoluteAdapterPosition).topic
                    topicListener.onTopicClicked(
                        topic.topicName,
                        topic.maxMessageId,
                        topic.streamId
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type

    class StreamViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        internal var groupTitle: TextView = itemView.findViewById(R.id.title_text_view)
        internal var expandArrow: ImageView = itemView.findViewById(R.id.expand_arrow)
        internal var collapseArrow: ImageView = itemView.findViewById(R.id.collapse_arrow)
    }

    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var topic: TextView = itemView.findViewById(R.id.channel_text_view)
    }
}

class ChannelsComparator : DiffUtil.ItemCallback<ExpandableStreamModel>() {

    override fun areItemsTheSame(
        oldItem: ExpandableStreamModel,
        newItem: ExpandableStreamModel
    ): Boolean {
        if (oldItem.type == ExpandableStreamModel.PARENT &&
            newItem.type == ExpandableStreamModel.PARENT
        ) {
            return oldItem.stream.id == newItem.stream.id

        } else if (oldItem.type == ExpandableStreamModel.CHILD &&
            newItem.type == ExpandableStreamModel.CHILD
        ) {
            return oldItem.topic.maxMessageId == newItem.topic.maxMessageId
        }
        return false
    }

    override fun areContentsTheSame(
        oldItem: ExpandableStreamModel,
        newItem: ExpandableStreamModel
    ): Boolean {
        if (oldItem.type == ExpandableStreamModel.PARENT &&
            newItem.type == ExpandableStreamModel.PARENT
        ) {
            return oldItem.stream == newItem.stream &&
                oldItem.isExpanded == newItem.isExpanded

        } else if (oldItem.type == ExpandableStreamModel.CHILD &&
            newItem.type == ExpandableStreamModel.CHILD
        ) {
            return oldItem.topic == newItem.topic
        }
        return false
    }
}
