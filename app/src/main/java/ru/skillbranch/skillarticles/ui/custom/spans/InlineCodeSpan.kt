package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting

class InlineCodeSpan(
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val bgColor: Int,
    @Px
    private val cornerRadius: Float,
    @Px
    private val padding: Float
) : ReplacementSpan() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect: RectF = RectF()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var measureWidth: Int = 0
    lateinit var bounds: IntRange

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        bounds = start..end
        paint.forText {
            val measureText = paint.measureText(text.toString(), start, end)
            measureWidth = (measureText + 2 * padding).toInt()
            fm?.top = paint.fontMetrics.top.toInt()
        }
        return measureWidth
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {

        paint.forBackground {
            rect.set(x, top.toFloat(), x + measureWidth, y + paint.descent())
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        }

        paint.forText {
            canvas.drawText(text, start, end, x + padding, y.toFloat(), paint)
        }
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldSize = textSize
        val oldStyle = typeface?.style ?: 0
        val oldFont = typeface
        val oldColor = color

        color = textColor
        typeface = Typeface.create(Typeface.MONOSPACE, oldStyle)
        textSize *= 0.85f

        block()

        color = oldColor
        typeface = oldFont
        textSize = oldSize
    }

    private inline fun Paint.forBackground(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style

        color = bgColor
        style = Paint.Style.FILL

        block()

        color = oldColor
        style = oldStyle
    }

    fun getExtraPadding(spanStart: Int, spanEnd: Int, horizontalPadding: Int) : Pair<Int, Int> {
        var startPad = 0
        var endPad = 0
        if(spanStart != bounds.first) startPad = (padding).toInt() + horizontalPadding
        if(spanEnd != bounds.last) endPad = -horizontalPadding
        return startPad to endPad
    }
}