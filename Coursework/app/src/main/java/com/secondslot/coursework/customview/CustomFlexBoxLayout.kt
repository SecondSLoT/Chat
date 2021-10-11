package com.secondslot.coursework.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.secondslot.coursework.extentions.toPx

class CustomFlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var currentRight = paddingLeft
        var totalWidth = 0
        var totalHeight = paddingTop + paddingBottom
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        var child: View? = null
        var childMarginTop = 0
        var childMarginBottom = 0

        for (i in 0 until childCount) {
            child = getChildAt(i)

            measureChildWithMargins(
                child,
                widthMeasureSpec,
                paddingLeft,
                heightMeasureSpec,
                totalHeight
            )

            val childMarginLeft = (child.layoutParams as MarginLayoutParams).leftMargin
            val childMarginRight = (child.layoutParams as MarginLayoutParams).rightMargin
            childMarginTop = (child.layoutParams as MarginLayoutParams).topMargin
            childMarginBottom = (child.layoutParams as MarginLayoutParams).bottomMargin

            if (currentRight + child.measuredWidth +
                childMarginLeft + childMarginRight + paddingRight > maxWidth
            ) {
                totalWidth = maxWidth
                currentRight = paddingLeft
                totalHeight += child.measuredHeight +
                    childMarginTop + childMarginBottom + CHILD_PADDING_BOTTOM_DIP.toPx.toInt()
            }

            currentRight += child.measuredWidth + childMarginLeft + childMarginRight +
                CHILD_PADDING_RIGHT_DIP.toPx.toInt()
        }

        if (child != null) {
            totalHeight += child.measuredHeight +
                childMarginTop + childMarginBottom
        }

        val resultWidth =
            resolveSize(maxOf(totalWidth, currentRight + paddingRight), widthMeasureSpec)
        val resultHeight = resolveSize(totalHeight, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var currentRight = paddingLeft
        var currentBottom = paddingTop

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            val childMarginLeft = (child.layoutParams as MarginLayoutParams).leftMargin
            val childMarginRight = (child.layoutParams as MarginLayoutParams).rightMargin

            if (currentRight + child.measuredWidth + childMarginLeft +
                childMarginRight + paddingRight < width
            ) {
                child.layout(
                    currentRight,
                    currentBottom,
                    currentRight + child.measuredWidth,
                    currentBottom + child.measuredHeight
                )
            } else {
                currentRight = paddingLeft
                currentBottom += child.measuredHeight + CHILD_PADDING_BOTTOM_DIP.toPx.toInt()

                child.layout(
                    currentRight,
                    currentBottom,
                    currentRight + child.measuredWidth,
                    currentBottom + child.measuredHeight
                )
            }
            currentRight += child.measuredWidth + childMarginLeft + childMarginRight +
                CHILD_PADDING_RIGHT_DIP.toPx.toInt()
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    companion object {
        private const val CHILD_PADDING_RIGHT_DIP = 8
        private const val CHILD_PADDING_BOTTOM_DIP = 6
    }
}
