package com.secondslot.coursework.features.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.databinding.ItemMessageMenuBinding
import com.secondslot.coursework.features.chat.listener.MessageMenuListener
import com.secondslot.coursework.features.chat.model.MessageMenuItem

class MessageMenuAdapter(
    private val menuList: List<MessageMenuItem>,
    private val listener: MessageMenuListener
) : RecyclerView.Adapter<MessageMenuAdapter.MessageMenuHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageMenuHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageMenuBinding.inflate(layoutInflater, parent, false)
        return MessageMenuHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: MessageMenuHolder, position: Int) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size

    class MessageMenuHolder(
        private val binding: ItemMessageMenuBinding,
        private val listener: MessageMenuListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MessageMenuItem) {
            binding.run {
                icon.setImageDrawable(
                    AppCompatResources.getDrawable(itemView.context, menuItem.icon)
                )
                name.text = itemView.context.getString(menuItem.name)
            }

            itemView.setOnClickListener { listener.onMessageMenuItemClick(menuItem.id) }
        }
    }
}
