package com.secondslot.coursework.other

import android.content.ClipData
import android.content.ClipboardManager

class MyClipboardManager(
    private val clipboard: ClipboardManager
) {

    fun copyToClipboard(text: String) {
        val clip: ClipData = ClipData.newPlainText("simple text", text)
        clipboard.setPrimaryClip(clip)
    }
}
