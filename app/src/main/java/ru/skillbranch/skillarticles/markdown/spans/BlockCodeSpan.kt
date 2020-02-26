package ru.skillbranch.skillarticles.markdown.spans


import android.graphics.*
import android.text.style.ReplacementSpan
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

    private val linePadding = 0.4f

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        fm ?: return 0

        when (type) {
            Element.BlockCode.Type.START -> {
                fm.ascent = (fm.ascent - paint.textSize * 1.4f).toInt()
            }
            Element.BlockCode.Type.END ->
                fm.descent = (fm.descent + paint.textSize * 1.4f).toInt()
            Element.BlockCode.Type.MIDDLE -> {
            }
            Element.BlockCode.Type.SINGLE -> {
                fm.ascent = (fm.ascent - paint.textSize * 1.4f).toInt()
                fm.descent = (fm.descent + paint.textSize * 1.4f).toInt()
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
        val ascent = paint.fontMetrics.ascent
        val descent = paint.fontMetrics.descent
        paint.forBackground {
            when (type) {
                Element.BlockCode.Type.START -> {
                    rect.set(
                        0f,
                        top.toFloat() + paint.textSize,
                        canvas.width.toFloat(),
                        bottom.toFloat()
                    )
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                    rect.set(
                        0f,
                        (bottom + paint.textSize + top) / 2f,
                        canvas.width.toFloat(),
                        bottom.toFloat()
                    )
                    canvas.drawRect(rect, paint)
                }
                Element.BlockCode.Type.END -> {
                    rect.set(
                        0f,
                        top.toFloat(),
                        canvas.width.toFloat(),
                        bottom.toFloat() - paint.textSize
                    )
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                    rect.set(
                        0f,
                        top.toFloat(),
                        canvas.width.toFloat(),
                        (bottom - paint.textSize + top) / 2f
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
                        top.toFloat() + paint.textSize,
                        canvas.width.toFloat(),
                        bottom.toFloat() - paint.textSize
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
}
