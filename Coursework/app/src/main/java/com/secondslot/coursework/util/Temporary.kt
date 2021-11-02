package com.secondslot.coursework.util

object Temporary {

    private var counter = 0
    private const val MAX = 4

    fun imitateError(): Boolean {
        return if (counter == MAX) {
            counter = 0
            true
        } else {
            counter++
            false
        }
    }
}
