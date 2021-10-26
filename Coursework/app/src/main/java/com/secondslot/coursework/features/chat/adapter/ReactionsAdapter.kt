package com.secondslot.coursework.features.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.databinding.ItemEmojiBinding
import com.secondslot.coursework.domain.model.Reaction
import com.secondslot.coursework.features.chat.ui.ChooseReactionListener

class ReactionsAdapter(
    private val reactions: List<Reaction>,
    private val listener: ChooseReactionListener
) :
    RecyclerView.Adapter<ReactionsAdapter.ReactionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemEmojiBinding.inflate(layoutInflater, parent, false)
        return ReactionHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ReactionHolder, position: Int) {
        return holder.bind(reactions)
    }

    override fun getItemCount(): Int = reactions.size

    inner class ReactionHolder(
        private val binding: ItemEmojiBinding,
        private val listener: ChooseReactionListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.reactionChosen(reactions.get(bindingAdapterPosition))
            }
        }

        fun bind(reactions: List<Reaction>) {
            binding.emoji.text = reactions[bindingAdapterPosition].code
        }
    }
}
