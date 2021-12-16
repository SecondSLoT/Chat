package com.secondslot.coursework.extentions

import android.os.Build
import android.text.Html
import java.lang.NumberFormatException

fun String.convertEmojiCode(): String {
    val secondPart = this.substringAfter("-", "")

    return if (secondPart == "") {
        try {
            String(Character.toChars(this.toInt(16)))
        } catch (e: NumberFormatException) {
            this
        }
    } else {
        val firstPart = this.substringBefore("-", this)
        String(Character.toChars(firstPart.toInt(16))) +
            String(Character.toChars(secondPart.toInt(16)))
    }
}

fun String.fromHtml(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT).toString().trim()
    } else {
        Html.fromHtml(this).toString().trim()
    }
}
