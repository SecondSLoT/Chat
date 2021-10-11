package com.secondslot.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.secondslot.coursework.customview.CustomFlexBoxLayout
import com.secondslot.coursework.customview.CustomReactionView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val customFlexBoxLayout = findViewById<CustomFlexBoxLayout>(R.id.custom_flex_box_layout)
        val addReactionButton = findViewById<ImageView>(R.id.add_reaction_button)
//        val testAddReactionButton = findViewById<Button>(R.id.add_button)

        addReactionButton.setOnClickListener {
            val reactionView = CustomReactionView(this)
            val index = customFlexBoxLayout.childCount - 1
            customFlexBoxLayout.addView(reactionView, index)
        }

//        testAddReactionButton.setOnClickListener {
//            val reactionView = CustomReactionView(this)
//            val index = customFlexBoxLayout.childCount - 1
//            customFlexBoxLayout.addView(reactionView, index)
//        }
    }
}
