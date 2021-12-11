package com.secondslot.coursework.extentions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Formatting Unix time in seconds to human readable String
 */
fun Number.getDateForChat(): String {
    val time = Date(this.toLong() * 1000)
    val df: DateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
    return df.format(time).uppercase()
}
