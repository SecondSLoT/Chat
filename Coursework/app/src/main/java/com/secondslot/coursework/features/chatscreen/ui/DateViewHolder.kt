package com.secondslot.coursework.features.chatscreen.ui

import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.databinding.ItemDateDividerBinding
import com.secondslot.coursework.domain.model.DateDivider

class DateViewHolder(
    private val binding: ItemDateDividerBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(dateDivider: DateDivider) {
        binding.dateTextView.text = dateDivider.date
    }
}