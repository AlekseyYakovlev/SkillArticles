package ru.skillbranch.skillarticles.markdown.spans


import android.graphics.*
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import android.text.style.ReplacementSpan
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.markdown.Element


class BlockCodeSpan(
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val bgColor: Int,
    @Px
    private val cornerRadius: Float,
    @Px
    private val padding: Float,
    private val type: Element.BlockCode.Type
) : ReplacementSpan() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect = RectF()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

    private val codeMagnifier = 0.85f
    private val codeMagnifier1 = 0.85f

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        fm ?: return 0

        val defaultAscent = paint.ascent()
        val defaultDescent = paint.descent()

        when (type) {
            Element.BlockCode.Type.START -> {
                fm.ascent = (defaultAscent * codeMagnifier1 - 2 * padding).toInt()
                fm.descent = (defaultDescent * codeMagnifier1).toInt()
            }
            Element.BlockCode.Type.END -> {
                fm.ascent = (defaultAscent * codeMagnifier1).toInt()
                fm.descent = (defaultDescent * codeMagnifier1 + 2 * padding).toInt()
            }
            Element.BlockCode.Type.MIDDLE -> {
                fm.ascent = (defaultAscent * codeMagnifier1).toInt()
                fm.descent = (defaultDescent * codeMagnifier1).toInt()
            }
            Element.BlockCode.Type.SINGLE -> {
                fm.ascent = (defaultAscent * codeMagnifier1 - 2 * padding).toInt()
                fm.descent = (defaultDescent * codeMagnifier1 + 2 * padding).toInt()
            }
        }
        return 0
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
            when (type) {
                Element.BlockCode.Type.START -> {
                    rect.set(
                        0f,
                        top + padding,
                        x + canvas.width.toFloat(),
                        bottom.toFloat()
                    )
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                    rect.set(
                        0f,
                        (bottom + padding + top) / 2f,
                        x + canvas.width.toFloat(),
                        bottom.toFloat()
                    )
                    canvas.drawRect(rect, paint)
                }
                Element.BlockCode.Type.END -> {
                    rect.set(
                        0f,
                        top.toFloat(),
                        x + canvas.width.toFloat(),
                        bottom.toFloat() - padding
                    )
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                    rect.set(
                        0f,
                        top.toFloat(),
                        x + canvas.width.toFloat(),
                        (bottom - padding + top) / 2f
                    )
                    canvas.drawRect(rect, paint)
                }
                Element.BlockCode.Type.MIDDLE -> {
                    rect.set(0f, top.toFloat(), canvas.width.toFloat(), bottom.toFloat())
                    canvas.drawRect(rect, paint)
                }
                Element.BlockCode.Type.SINGLE -> {
                    rect.set(
                        0f,
                        top.toFloat() + padding,
                        x + canvas.width.toFloat(),
                        bottom.toFloat() - padding
                    )
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                }
            }
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
        textSize *= codeMagnifier

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

}
