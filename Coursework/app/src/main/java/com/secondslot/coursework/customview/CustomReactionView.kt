package com.secondslot.coursework.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.secondslot.coursework.R
import com.secondslot.coursework.extentions.toPx

class CustomReactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var emoji = ""
        set(value) {
            field = value
            requestLayout()
        }

    var counter = 0
        set(value) {
            field = value
            requestLayout()
        }

    var contentString = ""

    // Передаём ANTI_ALIAS_FLAG в конструктор для того, чтобы текст рисовался более плавным
    // Ссылка на статью с объяснением https://medium.com/@ali.muzaffar/android-why-your-canvas-shapes-arent-smooth-aa2a3f450eb5
    private val contentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val contentBounds = Rect()
    private val contentCoordinate = PointF()

    // Про font metrics https://proandroiddev.com/android-and-typography-101-5f06722dd611
    private val tempFontMetrics = Paint.FontMetrics()

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val backgroundBounds = RectF()
    private val cornerRadius: Float
    private var selectedBgColor: Int
    private var unselectedBgColor: Int

    init {
        val styledAttrs: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomReactionView,
            defStyleAttr,
            defStyleRes
        )

        emoji =
            styledAttrs.getString(R.styleable.CustomReactionView_reaction) ?: "\uD83D\uDE0A"

        contentPaint.run {
            color =
                styledAttrs.getColor(
                    R.styleable.CustomReactionView_reactionTextColor,
                    ContextCompat.getColor(context, R.color.custom_reaction_text)
                )

            textSize =
                styledAttrs.getDimension(
                    R.styleable.CustomReactionView_reactionTextSize,
                    DEFAULT_CONTENT_TEXT_SIZE_DP.toPx
                )
        }

        selectedBgColor = styledAttrs.getColor(
            R.styleable.CustomReactionView_reactionSelectedColor,
            ContextCompat.getColor(context, R.color.bg_custom_reaction_selected)
        )

        unselectedBgColor = styledAttrs.getColor(
            R.styleable.CustomReactionView_reactionUnselectedColor,
            ContextCompat.getColor(context, R.color.on_background)
        )

        setBackgroundPaintColor(isSelected)

        cornerRadius = styledAttrs.getDimension(
            R.styleable.CustomReactionView_reactionCornerRadius,
            DEFAULT_BACKGROUND_CORNER_RADIUS_DP.toPx
        )

        styledAttrs.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        constructContentString()
        contentPaint.getTextBounds(contentString, 0, contentString.length, contentBounds)

        val textHeight = contentBounds.height()
        val textWidth = contentBounds.width()

        val totalWidth =
            textWidth + paddingRight + paddingLeft + 2 * MARGIN_HORIZONTAL_DP.toPx.toInt()
        val totalHeight =
            textHeight + paddingTop + paddingBottom + 2 * MARGIN_VERTICAL_DP.toPx.toInt()

        val resultWidth = resolveSize(totalWidth, widthMeasureSpec)
        val resultHeight = resolveSize(totalHeight, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        contentPaint.getFontMetrics(tempFontMetrics)
        contentCoordinate.x = w / 2f
        contentCoordinate.y = h / 2f + contentBounds.height() / 2 - tempFontMetrics.descent

        backgroundBounds.right = w.toFloat()
        backgroundBounds.bottom = h.toFloat()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState =
            super.onCreateDrawableState(extraSpace + SUPPORTED_DRAWABLE_STATE.size)
        if (isSelected) {
            mergeDrawableStates(drawableState, SUPPORTED_DRAWABLE_STATE)
        }
        return drawableState
    }

    override fun onDraw(canvas: Canvas) {
        setBackgroundPaintColor(isSelected)
        canvas.drawRoundRect(backgroundBounds, cornerRadius, cornerRadius, backgroundPaint)

        canvas.drawText(contentString, contentCoordinate.x, contentCoordinate.y, contentPaint)
    }

    private fun constructContentString() {
        contentString = "$emoji $counter"
    }

    private fun setBackgroundPaintColor(isSelected: Boolean) {
        backgroundPaint.color = if (isSelected) selectedBgColor else unselectedBgColor
    }

    companion object {
        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
        private const val DEFAULT_CONTENT_TEXT_SIZE_DP = 14
        private const val DEFAULT_BACKGROUND_CORNER_RADIUS_DP = 10
        private const val MARGIN_HORIZONTAL_DP = 8
        private const val MARGIN_VERTICAL_DP = 6
    }
}
