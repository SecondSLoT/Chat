package com.secondslot.coursework.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.CustomMessageViewGroupBinding
import com.secondslot.coursework.extentions.toPx

class CustomMessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: CustomMessageViewGroupBinding =
        CustomMessageViewGroupBinding.inflate(LayoutInflater.from(context), this)

    private val messageBackgroundBounds = RectF()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
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

        // Add a new reaction when AddReactionButton is clicked
        binding.addReactionButton.setOnClickListener {
            val reactionView = CustomReactionView(context)
            val index = binding.customFlexBoxLayout.childCount - 1
            binding.customFlexBoxLayout.addView(reactionView, index)
        }

        styledAttrs.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Check if childCount equals expected value
        require(childCount == CHILD_COUNT) {
            context.getString(
                R.string.custom_message_view_group_child_count_error,
                CHILD_COUNT,
                childCount
            )
        }

        val avatarImageView = getChildAt(0)
        val personNameTextView = getChildAt(1)
        val messageTextView = getChildAt(2)
        val reactionsLayout = getChildAt(3)

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

        // Measure personNameTextView
        measureChildWithMargins(
            personNameTextView,
            widthMeasureSpec,
            avatarImageView.measuredWidth + avatarMarginLeft + avatarMarginRight,
            heightMeasureSpec,
            0
        )

        val personMarginLeft = (personNameTextView.layoutParams as MarginLayoutParams).leftMargin
        val personMarginRight = (personNameTextView.layoutParams as MarginLayoutParams).rightMargin
        val personMarginTop = (personNameTextView.layoutParams as MarginLayoutParams).bottomMargin
        val personMarginBottom =
            (personNameTextView.layoutParams as MarginLayoutParams).bottomMargin
        totalWidth += personNameTextView.measuredWidth + personMarginLeft + personMarginRight
        totalHeight = maxOf(
            totalHeight,
            personNameTextView.measuredHeight + personMarginTop + personMarginBottom
        )

        // Measure messageTextView
        measureChildWithMargins(
            messageTextView,
            widthMeasureSpec,
            avatarImageView.measuredWidth + avatarMarginLeft + avatarMarginRight,
            heightMeasureSpec,
            0
        )

        val messageMarginLeft = (messageTextView.layoutParams as MarginLayoutParams).leftMargin
        val messageMarginRight = (messageTextView.layoutParams as MarginLayoutParams).rightMargin
        val messageMarginTop = (messageTextView.layoutParams as MarginLayoutParams).bottomMargin
        val messageMarginBottom =
            (messageTextView.layoutParams as MarginLayoutParams).bottomMargin
        totalWidth += messageTextView.measuredWidth + messageMarginLeft + messageMarginRight
        totalHeight = maxOf(
            totalHeight,
            personNameTextView.measuredHeight + personMarginTop + personMarginBottom +
                messageTextView.measuredHeight + messageMarginTop + messageMarginBottom
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
        val personNameTextView = getChildAt(1)
        val messageTextView = getChildAt(2)
        val flexBoxLayout = getChildAt(3)

        // Place avatarImageView
        avatarImageView.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + avatarImageView.measuredWidth,
            paddingTop + avatarImageView.measuredHeight
        )

        val avatarMarginRight = (avatarImageView.layoutParams as MarginLayoutParams).rightMargin

        // Place personNameTextView
        personNameTextView.layout(
            avatarImageView.right + avatarMarginRight,
            paddingTop,
            avatarImageView.right + avatarMarginRight + personNameTextView.measuredWidth,
            paddingTop + personNameTextView.measuredHeight
        )

        val personNameMarginBottom =
            (personNameTextView.layoutParams as MarginLayoutParams).bottomMargin

        // Place messageTextView
        messageTextView.layout(
            avatarImageView.right + avatarMarginRight,
            personNameTextView.bottom + personNameMarginBottom,
            avatarImageView.right + avatarMarginRight + messageTextView.measuredWidth,
            personNameTextView.bottom + personNameMarginBottom + messageTextView.measuredHeight
        )

        val messageMarginBottom =
            (messageTextView.layoutParams as MarginLayoutParams).bottomMargin

        // Place reactionsLayout
        flexBoxLayout.layout(
            avatarImageView.right + avatarMarginRight,
            messageTextView.bottom + messageMarginBottom,
            avatarImageView.right + avatarMarginRight + flexBoxLayout.measuredWidth,
            messageTextView.bottom + messageMarginBottom + flexBoxLayout.measuredHeight
        )

        messageBackgroundBounds.left = (avatarImageView.right + avatarMarginRight).toFloat()
        messageBackgroundBounds.top = paddingTop.toFloat()
        messageBackgroundBounds.right = (messageTextView.right).toFloat()
        messageBackgroundBounds.bottom = (messageTextView.bottom).toFloat()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.drawRoundRect(
            messageBackgroundBounds,
            MESSAGE_BACKGROUND_CORNER_RADIUS_DP.toPx,
            MESSAGE_BACKGROUND_CORNER_RADIUS_DP.toPx,
            backgroundPaint
        )
        super.dispatchDraw(canvas)
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

    fun setPersonPhoto(image: Drawable) {
        binding.personPhoto.background = image
    }

    fun setPersonName(name: String) {
        binding.personName.text = name
    }

    fun setMessageText(message: String) {
        binding.messageTextView.text = message
    }

    fun changeReactionSelectedState(index: Int) {
        if (binding.customFlexBoxLayout.childCount > 0 &&
            index in 0 until binding.customFlexBoxLayout.childCount
        ) {

            val child = binding.customFlexBoxLayout.getChildAt(index)
            child.isSelected = !child.isSelected
        }
    }

    fun getReactionCount(index: Int): Int {
        if (binding.customFlexBoxLayout.childCount > 0 &&
            index in 0 until binding.customFlexBoxLayout.childCount - 1
        ) {

            val child = binding.customFlexBoxLayout.getChildAt(index) as CustomReactionView
            return child.counter
        }
        return -1
    }

    companion object {
        private const val CHILD_COUNT = 4
        private const val MESSAGE_BACKGROUND_CORNER_RADIUS_DP = 18
    }
}
