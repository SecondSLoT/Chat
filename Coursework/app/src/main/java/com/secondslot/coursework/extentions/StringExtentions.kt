package com.secondslot.coursework.extentions

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
