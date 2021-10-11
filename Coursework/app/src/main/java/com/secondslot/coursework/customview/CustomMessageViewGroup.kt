package com.secondslot.coursework.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.ViewGroup
import com.secondslot.coursework.R

class CustomMessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val messageBackgroundBounds = RectF()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
        inflate(context, R.layout.custom_message_view_group, this)

        val styledAttrs: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomMessageViewGroup,
            defStyleAttr,
            defStyleRes
        )

        backgroundPaint.color = styledAttrs.getColor(
            R.styleable.CustomMessageViewGroup_customBackgroundColor,
            Color.parseColor("#282828")
        )

        styledAttrs.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        require(childCount == 3) { "ChildCount expected 3, but found $childCount" }

        val avatarImageView = getChildAt(0)
        val messageLayout = getChildAt(1)
        val reactionsLayout = getChildAt(2)

        var totalWidth = 0
        var totalHeight = 0

        // Measure avatarImageView
        measureChildWithMargins(
            avatarImageView,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        val avatarMarginLeft = (avatarImageView.layoutParams as MarginLayoutParams).leftMargin
        val avatarMarginRight = (avatarImageView.layoutParams as MarginLayoutParams).rightMargin
        totalWidth += avatarImageView.measuredWidth + avatarMarginLeft + avatarMarginRight
        totalHeight = maxOf(totalHeight, avatarImageView.measuredHeight)

        // Measure messageLayout
        measureChildWithMargins(
            messageLayout,
            widthMeasureSpec,
            avatarImageView.measuredWidth + avatarMarginLeft + avatarMarginRight,
            heightMeasureSpec,
            0
        )

        val messageMarginLeft = (messageLayout.layoutParams as MarginLayoutParams).leftMargin
        val messageMarginRight = (messageLayout.layoutParams as MarginLayoutParams).rightMargin
        val messageMarginTop = (messageLayout.layoutParams as MarginLayoutParams).bottomMargin
        val messageMarginBottom = (messageLayout.layoutParams as MarginLayoutParams).bottomMargin
        totalWidth += messageLayout.measuredWidth + messageMarginLeft + messageMarginRight
        totalHeight = maxOf(
            totalHeight,
            messageLayout.measuredHeight + messageMarginTop + messageMarginBottom
        )

        // Measure reactionsLayout
        measureChildWithMargins(
            reactionsLayout,
            widthMeasureSpec,
            avatarImageView.measuredWidth + avatarMarginLeft + avatarMarginRight,
            heightMeasureSpec,
            totalHeight
        )

        val flexBoxLayoutMarginLeft =
            (reactionsLayout.layoutParams as MarginLayoutParams).leftMargin
        val flexBoxLayoutMarginRight =
            (reactionsLayout.layoutParams as MarginLayoutParams).rightMargin
        val flexBoxLayoutMarginTop =
            (reactionsLayout.layoutParams as MarginLayoutParams).topMargin
        val flexBoxLayoutMarginBottom =
            (reactionsLayout.layoutParams as MarginLayoutParams).bottomMargin
        totalWidth = maxOf(
            totalWidth,
            avatarImageView.measuredWidth + avatarMarginLeft + avatarMarginRight +
                reactionsLayout.measuredWidth + flexBoxLayoutMarginLeft + flexBoxLayoutMarginRight
        )
        totalHeight += reactionsLayout.measuredHeight + flexBoxLayoutMarginTop +
            flexBoxLayoutMarginBottom

        val resultWidth =
            resolveSize(totalWidth + paddingRight + paddingLeft, widthMeasureSpec)
        val resultHeight =
            resolveSize(totalHeight + paddingTop + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val avatarImageView = getChildAt(0)
        val messageLayout = getChildAt(1)
        val flexBoxLayout = getChildAt(2)

        // Place avatarImageView
        avatarImageView.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + avatarImageView.measuredWidth,
            paddingTop + avatarImageView.measuredHeight
        )

        val avatarMarginRight = (avatarImageView.layoutParams as MarginLayoutParams).rightMargin

        // Place messageLayout
        messageLayout.layout(
            avatarImageView.right + avatarMarginRight,
            paddingTop,
            avatarImageView.right + avatarMarginRight + messageLayout.measuredWidth,
            paddingTop + messageLayout.measuredHeight
        )

        val messageMarginRight = (messageLayout.layoutParams as MarginLayoutParams).rightMargin
        val messageMarginBottom = (messageLayout.layoutParams as MarginLayoutParams).bottomMargin

        // Place reactionsLayout
        flexBoxLayout.layout(
            avatarImageView.right + avatarMarginRight,
            messageLayout.bottom + messageMarginBottom,
            avatarImageView.right + avatarMarginRight + flexBoxLayout.measuredWidth,
            messageLayout.bottom + messageMarginBottom + flexBoxLayout.measuredHeight
        )

        messageBackgroundBounds.left = (avatarImageView.right + avatarMarginRight).toFloat()
        messageBackgroundBounds.top = paddingTop.toFloat()
        messageBackgroundBounds.right = (messageLayout.right + messageMarginRight).toFloat()
        messageBackgroundBounds.bottom = (messageLayout.bottom + messageMarginBottom).toFloat()
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
}
