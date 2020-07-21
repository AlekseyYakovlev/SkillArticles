package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.text.style.ReplacementSpan
import androidx.annotation.VisibleForTesting

class UnderlineSpan(
    private val underlineColor: Int,
    dotWidth: Float = 6f
) : ReplacementSpan() {
    private var textWidth = 0
    private val dashs = DashPathEffect(floatArrayOf(dotWidth, dotWidth), 0f)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

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
        paint.forLine {
            path.reset()
            path.moveTo(x, y + paint.descent())
            path.lineTo(x + textWidth, y + paint.descent())
            canvas.drawPath(path, paint)
        }

        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }


    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        textWidth = paint.measureText(text.toString(), start, end).toInt()
        return textWidth
    }


    private inline fun Paint.forLine(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        val oldWidth = strokeWidth

        pathEffect = dashs
        color = underlineColor
        style = Paint.Style.STROKE
        strokeWidth = 0f

        block()

        color = oldColor
        style = oldStyle
        strokeWidth = oldWidth
    }
}