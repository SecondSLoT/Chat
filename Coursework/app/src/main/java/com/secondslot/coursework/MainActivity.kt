package com.secondslot.coursework

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.secondslot.coursework.customview.CustomMessageViewGroup

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // This section is just for demonstration
        val customMessageViewGroup =
            findViewById<CustomMessageViewGroup>(R.id.custom_message_view_group)
        val testButton = findViewById<Button>(R.id.test_button)
        testButton.setOnClickListener {
            customMessageViewGroup.run {
                setPersonPhoto(getDrawable(R.drawable.ic_launcher_background)!!)
                setPersonName("Test really long nickname that don't fit in one line")
                setMessageText("Some")
                changeReactionSelectedState(1)
            }
            val reactionsCount = customMessageViewGroup.getReactionCount(1)
            if (reactionsCount != -1) {
                Toast.makeText(
                    this,
                    "Reactions on 2nd emoji = $reactionsCount",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}
