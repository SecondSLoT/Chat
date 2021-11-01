package com.secondslot.coursework.features.people.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.extentions.toPx

class UsersItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.bottom = DIVIDER_HEIGHT_DP.toPx.toInt()
    }

    companion object {
        private const val DIVIDER_HEIGHT_DP = 16
    }
}
