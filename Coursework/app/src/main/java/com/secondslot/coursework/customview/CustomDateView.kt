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
import com.secondslot.coursework.R
import com.secondslot.coursework.extentions.getDateForChat
import com.secondslot.coursework.extentions.toPx

class CustomDateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var date = System.currentTimeMillis()
        set(value) {
            field = value
            requestLayout()
        }

    private val contentString = date.getDateForChat()

    private val contentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val contentBounds = Rect()
    private val contentCoordinate = PointF()
    private val tempFontMetrics = Paint.FontMetrics()

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val backgroundBounds = RectF()
    private val cornerRadius: Float
    private var bgColor: Int

    init {
        val styledAttrs: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomDateView,
            defStyleAttr,
            defStyleRes
        )

        date =
            styledAttrs.getString(R.styleable.CustomDateView_date)?.toLong() ?: 1000L

        contentPaint.run {
            color =
                styledAttrs.getColor(
                    R.styleable.CustomDateView_dateTextColor,
                    Color.parseColor("#999999")
                )

            textSize =
                styledAttrs.getDimension(
                    R.styleable.CustomDateView_dateTextSize,
                    DEFAULT_CONTENT_TEXT_SIZE_DP.toPx
                )
        }

        bgColor = styledAttrs.getColor(
            R.styleable.CustomDateView_dateBgColor,
            Color.parseColor("#070707")
        )

        backgroundPaint.color = bgColor

        cornerRadius = styledAttrs.getDimension(
            R.styleable.CustomDateView_dateCornerRadius,
            DEFAULT_BACKGROUND_CORNER_RADIUS_DP.toPx
        )

        styledAttrs.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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
        contentCoordinate.y = h / 2f + contentBounds.height() / 2

        backgroundBounds.right = w.toFloat()
        backgroundBounds.bottom = h.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(backgroundBounds, cornerRadius, cornerRadius, backgroundPaint)
        canvas.drawText(contentString, contentCoordinate.x, contentCoordinate.y, contentPaint)
    }

    companion object {
        private const val DEFAULT_CONTENT_TEXT_SIZE_DP = 14
        private const val DEFAULT_BACKGROUND_CORNER_RADIUS_DP = 58
        private const val MARGIN_HORIZONTAL_DP = 8
        private const val MARGIN_VERTICAL_DP = 6
    }
}
