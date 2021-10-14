package com.secondslot.coursework.customview

import android.content.Context
import android.util.AttributeSet
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
        var lineHeight = 0
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            measureChildWithMargins(
                child,
                widthMeasureSpec,
                paddingLeft,
                heightMeasureSpec,
                totalHeight
            )

            val childMarginLeft = (child.layoutParams as MarginLayoutParams).leftMargin
            val childMarginRight = (child.layoutParams as MarginLayoutParams).rightMargin
            val childMarginTop = (child.layoutParams as MarginLayoutParams).topMargin
            val childMarginBottom = (child.layoutParams as MarginLayoutParams).bottomMargin

            // Overall height of elements in one line should be by the largest element
            lineHeight = maxOf(
                lineHeight,
                child.measuredHeight + childMarginTop + childMarginBottom
            )

            if (currentRight + child.measuredWidth +
                childMarginLeft + childMarginRight + paddingRight > maxWidth
            ) {
                totalWidth = maxWidth
                currentRight = paddingLeft
                totalHeight += lineHeight + CHILD_PADDING_BOTTOM_DP.toPx.toInt()
                lineHeight = child.measuredHeight + childMarginTop + childMarginBottom
            }

            currentRight += child.measuredWidth + childMarginLeft + childMarginRight +
                CHILD_PADDING_RIGHT_DP.toPx.toInt()
        }

        // Calculate result width and height
        totalHeight += lineHeight

        val resultWidth =
            resolveSize(maxOf(totalWidth, currentRight + paddingRight), widthMeasureSpec)
        val resultHeight = resolveSize(totalHeight, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var currentRight = paddingLeft
        var currentBottom = paddingTop
        var lineHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            val childMarginLeft = (child.layoutParams as MarginLayoutParams).leftMargin
            val childMarginRight = (child.layoutParams as MarginLayoutParams).rightMargin
            val childMarginTop = (child.layoutParams as MarginLayoutParams).topMargin
            val childMarginBottom = (child.layoutParams as MarginLayoutParams).bottomMargin

            lineHeight = maxOf(
                lineHeight,
                child.measuredHeight + childMarginTop + childMarginBottom
            )

            if (currentRight + child.measuredWidth + childMarginLeft +
                childMarginRight + paddingRight <= width
            ) {
                child.layout(
                    currentRight,
                    currentBottom,
                    currentRight + child.measuredWidth,
                    currentBottom + child.measuredHeight
                )
            } else {
                currentRight = paddingLeft
                currentBottom += lineHeight + CHILD_PADDING_BOTTOM_DP.toPx.toInt()
                lineHeight = child.measuredHeight + childMarginTop + childMarginBottom

                child.layout(
                    currentRight,
                    currentBottom,
                    currentRight + child.measuredWidth,
                    currentBottom + child.measuredHeight
                )
            }
            currentRight += child.width + childMarginLeft + childMarginRight +
                CHILD_PADDING_RIGHT_DP.toPx.toInt()
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
        private const val CHILD_PADDING_RIGHT_DP = 8
        private const val CHILD_PADDING_BOTTOM_DP = 6
    }
}
