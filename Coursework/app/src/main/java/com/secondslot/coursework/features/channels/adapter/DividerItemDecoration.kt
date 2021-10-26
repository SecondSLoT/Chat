package com.secondslot.coursework.features.channels.adapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.R

class DividerItemDecoration() : RecyclerView.ItemDecoration() {

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dividerHeight = 1

    override fun getItemOffsets(
        rect: Rect,
        view: View,
        parent: RecyclerView,
        s: RecyclerView.State
    ) {
        super.getItemOffsets(rect, view, parent, s)

        rect.bottom = 1
        dividerPaint.color = ContextCompat.getColor(view.context, R.color.on_background)
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.children
            .forEach { view ->
                val left = parent.paddingLeft.toFloat()
                val top = view.bottom.toFloat()
                val right = (parent.width - parent.paddingRight).toFloat()
                val bottom = top + dividerHeight

                canvas.drawRect(left, top, right, bottom, dividerPaint)
            }
    }
}
