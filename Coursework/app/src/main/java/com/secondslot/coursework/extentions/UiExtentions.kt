package com.secondslot.coursework.extentions

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadRoundImage(
    imageUri: String,
) {

    val uri = Uri.parse("android.resource://com.secondslot.coursework/drawable/$imageUri")

    Glide.with(context)
        .load(uri)
        .circleCrop()
        .into(this)
}

fun ImageView.loadImage(
    imageUri: String,
) {

    val uri = Uri.parse("android.resource://com.secondslot.coursework/drawable/$imageUri")

    Glide.with(context)
        .load(uri)
        .into(this)
}
