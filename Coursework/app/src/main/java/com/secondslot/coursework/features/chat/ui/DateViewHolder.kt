package com.secondslot.coursework.features.chat.ui

import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.databinding.ItemDateDividerBinding
import com.secondslot.coursework.features.chat.model.DateDivider

class DateViewHolder(
    private val binding: ItemDateDividerBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(dateDivider: DateDivider) {
        binding.dateTextView.text = dateDivider.date
    }
}
