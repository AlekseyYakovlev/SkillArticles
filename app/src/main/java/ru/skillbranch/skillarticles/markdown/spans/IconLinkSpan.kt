package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting

class IconLinkSpan(
    private val linkDrawable: Drawable,
    @ColorInt
    private val iconColor: Int,
    @Px
    private val padding: Float,
    @ColorInt
    private val textColor: Int,
    dotWidth: Float = 6f
) : ReplacementSpan() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var iconSize = 0
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var textWidth = 0f
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
        //TODO implement me 1:37
        val textStart = x + iconSize + padding
        paint.forLine {
            path.reset()
            path.moveTo(textStart + textWidth, bottom.toFloat())
            canvas.drawPath(path, paint)
        }

        paint.forIcon {
            canvas.save()
            val trY = bottom - linkDrawable.bounds.bottom
            canvas.translate(x, trY.toFloat())
            linkDrawable.draw(canvas)
            canvas.restore()
        }

        paint.forText {
            canvas.drawText(text, start, end, textStart, y.toFloat(), paint)
        }
    }


    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        fm?.let {
            iconSize = it.descent - it.ascent //font size
            linkDrawable.setBounds(0, 0, iconSize, iconSize)
            linkDrawable.setTint(iconColor)
        }
        textWidth = paint.measureText(text.toString(), start, end)

        return (iconSize + padding + textWidth).toInt()
    }


    private inline fun Paint.forLine(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        val oldWidth = strokeWidth

        pathEffect = dashs
        color = textColor
        style = Paint.Style.STROKE
        strokeWidth = 0f

        block()

        color = oldColor
        style = oldStyle
        strokeWidth = oldWidth
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldColor = color

        color = textColor

        block()

        color = oldColor
    }

    private inline fun Paint.forIcon(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style

        color = textColor
        style = Paint.Style.STROKE

        block()

        color = oldColor
        style = oldStyle
    }
}