package com.secondslot.coursework.extentions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Long.getDateForChat(): String {
    val time = Date(this)
    val df: DateFormat = SimpleDateFormat("d MMM", Locale.ENGLISH)
    return df.format(time).uppercase()
}

