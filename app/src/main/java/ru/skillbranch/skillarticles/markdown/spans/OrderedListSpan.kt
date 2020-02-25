package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting


class OrderedListSpan(
    @Px
    private val gapWidth: Float,
    private val order: String,
    @ColorInt
    private val orderColor: Int
) : LeadingMarginSpan {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)

    override fun getLeadingMargin(first: Boolean): Int =
        (3 *gapWidth).toInt()

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence?, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout?
    ) {
        //draw bullet only for first line
        if(isFirstLine){
            paint.withCustomText {
                canvas.drawText(
                    order,
                    gapWidth + currentMarginLocation,
                    lineBaseline.toFloat(),
                    paint

                )
            }
        }
    }

    private inline fun Paint.withCustomText(block: () -> Unit) {
        val oldColor  = color
        val oldStyle = style

        color = orderColor
        style = Paint.Style.FILL

        block()

        color = oldColor
        style = oldStyle
    }
}