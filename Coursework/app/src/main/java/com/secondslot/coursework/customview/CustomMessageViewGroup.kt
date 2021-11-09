package com.secondslot.coursework.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.marginEnd
import com.secondslot.coursework.R
import com.secondslot.coursework.databinding.CustomMessageViewGroupBinding
import com.secondslot.coursework.domain.model.Reaction
import com.secondslot.coursework.extentions.loadRoundImage
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

    private var isOwnMessage = false

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
        val userNameTextView = getChildAt(1)
        val messageTextView = getChildAt(2)
        val reactionsLayout = getChildAt(3)

        var totalWidth = 0
        var totalHeight = 0

        if (!isOwnMessage) {
            avatarImageView.isGone = false
            userNameTextView.isGone = false

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

            // Measure userNameTextView
            measureChildWithMargins(
                userNameTextView,
                widthMeasureSpec,
                avatarImageView.measuredWidth + avatarMarginLeft + avatarMarginRight,
                heightMeasureSpec,
                0
            )

            val userMarginLeft =
                (userNameTextView.layoutParams as MarginLayoutParams).leftMargin
            val userMarginRight =
                (userNameTextView.layoutParams as MarginLayoutParams).rightMargin
            val userMarginTop =
                (userNameTextView.layoutParams as MarginLayoutParams).bottomMargin
            val userMarginBottom =
                (userNameTextView.layoutParams as MarginLayoutParams).bottomMargin
            totalWidth += userNameTextView.measuredWidth + userMarginLeft + userMarginRight
            totalHeight = maxOf(
                totalHeight,
                userNameTextView.measuredHeight + userMarginTop + userMarginBottom
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
            val messageMarginRight =
                (messageTextView.layoutParams as MarginLayoutParams).rightMargin
            val messageMarginTop = (messageTextView.layoutParams as MarginLayoutParams).bottomMargin
            val messageMarginBottom =
                (messageTextView.layoutParams as MarginLayoutParams).bottomMargin
            totalWidth += messageTextView.measuredWidth + messageMarginLeft + messageMarginRight
            totalHeight = maxOf(
                totalHeight,
                userNameTextView.measuredHeight + userMarginTop + userMarginBottom +
                    messageTextView.measuredHeight + messageMarginTop + messageMarginBottom
            )

            if (!reactionsLayout.isGone) {

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
                        reactionsLayout.measuredWidth + flexBoxLayoutMarginLeft +
                        flexBoxLayoutMarginRight
                )
                totalHeight += reactionsLayout.measuredHeight + flexBoxLayoutMarginTop +
                    flexBoxLayoutMarginBottom
            }
            // If it is own message
        } else {
            avatarImageView.isGone = true
            userNameTextView.isGone = true

            // Measure messageTextView
            measureChildWithMargins(
                messageTextView,
                widthMeasureSpec,
                messageTextView.marginEnd,
                heightMeasureSpec,
                0
            )

            val messageMarginLeft = (messageTextView.layoutParams as MarginLayoutParams).rightMargin
            val messageMarginRight =
                (messageTextView.layoutParams as MarginLayoutParams).leftMargin
            val messageMarginTop = (messageTextView.layoutParams as MarginLayoutParams).topMargin
            val messageMarginBottom =
                (messageTextView.layoutParams as MarginLayoutParams).bottomMargin
            totalWidth += messageTextView.measuredWidth + messageMarginLeft + messageMarginRight
            totalHeight = messageTextView.measuredHeight + messageMarginTop + messageMarginBottom

            if (!reactionsLayout.isGone) {

                // Measure reactionsLayout
                measureChildWithMargins(
                    reactionsLayout,
                    widthMeasureSpec,
                    messageTextView.marginEnd,
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
                    reactionsLayout.measuredWidth +
                        flexBoxLayoutMarginLeft + flexBoxLayoutMarginRight
                )
                totalHeight += reactionsLayout.measuredHeight + flexBoxLayoutMarginTop +
                    flexBoxLayoutMarginBottom
            }
        }

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
        val reactionsLayout = getChildAt(3)

        if (!isOwnMessage) {
            // Place avatarImageView
            avatarImageView.layout(
                paddingLeft,
                paddingTop,
                paddingLeft + avatarImageView.measuredWidth,
                paddingTop + avatarImageView.measuredHeight
            )

            val avatarMarginRight = (avatarImageView.layoutParams as MarginLayoutParams).rightMargin

            // Place userNameTextView
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

            if (!reactionsLayout.isGone) {
                // Place reactionsLayout
                reactionsLayout.layout(
                    avatarImageView.right + avatarMarginRight,
                    messageTextView.bottom + messageMarginBottom,
                    avatarImageView.right + avatarMarginRight + reactionsLayout.measuredWidth,
                    messageTextView.bottom + messageMarginBottom + reactionsLayout.measuredHeight
                )
            }

            messageBackgroundBounds.left = (avatarImageView.right + avatarMarginRight).toFloat()
            messageBackgroundBounds.top = paddingTop.toFloat()
            messageBackgroundBounds.right = maxOf(
                personNameTextView.right.toFloat(),
                messageTextView.right.toFloat()
            )
            messageBackgroundBounds.bottom = (messageTextView.bottom).toFloat()
            // If own message
        } else {
            // Place messageTextView
            messageTextView.layout(
                r - paddingRight - messageTextView.measuredWidth,
                t + paddingTop,
                r - paddingRight,
                t + paddingTop + messageTextView.measuredHeight
            )

            val messageMarginBottom =
                (messageTextView.layoutParams as MarginLayoutParams).bottomMargin

            if (!reactionsLayout.isGone) {
                // Place reactionsLayout
                reactionsLayout.layout(
                    r - paddingRight - messageTextView.width,
                    messageTextView.bottom + messageMarginBottom,
                    r - paddingRight,
                    messageTextView.bottom + messageMarginBottom + reactionsLayout.measuredHeight
                )
            }

            messageBackgroundBounds.left = (r - paddingRight - messageTextView.width).toFloat()
            messageBackgroundBounds.top = (t + paddingTop).toFloat()
            messageBackgroundBounds.right = (r - paddingRight).toFloat()
            messageBackgroundBounds.bottom =
                (t + paddingTop + messageTextView.measuredHeight).toFloat()
        }
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

    fun setUserPhoto(imageUri: String) {
        binding.userPhoto.loadRoundImage(imageUri)
    }

    fun setUsername(name: String) {
        binding.username.text = name
    }

    fun setMessageText(message: String) {
        binding.messageTextView.text = message
    }

    fun setReactions(reactions: List<Reaction>) {
        binding.customFlexBoxLayout.isGone = reactions.isEmpty()
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

    fun setMessageBgColor(bgColor: Int) {
        backgroundPaint.color = ContextCompat.getColor(context, bgColor)
    }

    fun setSelfMessageType(isSelf: Boolean) {
        isOwnMessage = isSelf
    }

    companion object {
        private const val CHILD_COUNT = 4
        private const val MESSAGE_BACKGROUND_CORNER_RADIUS_DP = 18
    }
}
