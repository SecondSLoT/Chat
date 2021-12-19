package com.secondslot.coursework.features.chat.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class MessageMenuItem(
    val id: Int,
    @StringRes val name: Int,
    @DrawableRes val icon: Int,
    val onlyForMessageAuthor: Boolean
)
