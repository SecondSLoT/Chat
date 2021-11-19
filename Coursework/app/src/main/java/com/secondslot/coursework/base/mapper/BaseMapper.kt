package com.secondslot.coursework.base.mapper

interface BaseMapper<in A, out B> {

    fun map(type: A?): B
}
