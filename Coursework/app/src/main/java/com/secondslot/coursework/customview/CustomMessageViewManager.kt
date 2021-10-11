package com.secondslot.coursework.customview

import android.graphics.drawable.Drawable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.secondslot.coursework.R

class CustomMessageViewManager(private val view: CustomMessageViewGroup) {

    private val personPhoto = view.findViewById<ImageView>(R.id.person_photo)
    private val personName = view.findViewById<TextView>(R.id.person_name)
    private val messageTextView = view.findViewById<TextView>(R.id.message_text_view)
    private val flexBoxLayout = view.findViewById<CustomFlexBoxLayout>(R.id.custom_flex_box_layout)
    private val addReactionButton = view.findViewById<ImageButton>(R.id.add_reaction_button)

    init {
        addReactionButton.setOnClickListener {
            val reactionView = CustomReactionView(view.context)
            val index = flexBoxLayout.childCount - 1
            flexBoxLayout.addView(reactionView, index)
        }
    }

    fun setPersonPhoto(image: Drawable) {
        personPhoto.background = image
    }

    fun setPersonName(name: String) {
        personName.text = name
    }

    fun setMessageText(message: String) {
        messageTextView.text = message
    }

    fun selectReaction(index: Int) {
        if (flexBoxLayout.childCount > 0 && index in 0 until flexBoxLayout.childCount) {
            val child = flexBoxLayout.getChildAt(index)
            child.isSelected = !child.isSelected
        }
    }
}
