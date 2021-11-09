package com.secondslot.coursework.extentions

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadRoundImage(
    imageUri: String,
) {

    Glide.with(context)
        .load(imageUri)
        .circleCrop()
        .into(this)
}

fun ImageView.loadImage(
    imageUri: String,
) {

    Glide.with(context)
        .load(imageUri)
        .into(this)
}
