package com.secondslot.coursework.core.mapper

interface BaseMapper<in A, out B> {

    fun map(type: A?): B
}
