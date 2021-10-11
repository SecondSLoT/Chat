package com.secondslot.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.secondslot.coursework.customview.CustomMessageViewGroup
import com.secondslot.coursework.customview.CustomMessageViewManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // This section is just for demonstration
        val customMessageViewManager =
            findViewById<CustomMessageViewGroup>(R.id.custom_message_view_group)
        val messageViewManager = CustomMessageViewManager(customMessageViewManager)
        val testButton = findViewById<Button>(R.id.test_button)
        testButton.setOnClickListener {
            messageViewManager.run {
                setPersonPhoto(getDrawable(R.drawable.ic_launcher_background)!!)
                setPersonName("Test Name")
                setMessageText("Some example message text")
                selectReaction(1)
            }
        }
    }
}
