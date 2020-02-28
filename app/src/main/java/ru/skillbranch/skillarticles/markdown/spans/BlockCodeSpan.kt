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

    companion object {
        private const val FONT_SCALE = 0.85f
    }

    private val startBlockRadius = floatArrayOf(
        cornerRadius, cornerRadius, // Top left radius in px
        cornerRadius, cornerRadius, // Top right radius in px
        0f, 0f,                     // Bottom right radius in px
        0f, 0f                      // Bottom left radius in px
    )
    private val endBlockRadius = floatArrayOf(
        0f, 0f,                     // Top left radius in px
        0f, 0f,                     // Top right radius in px
        cornerRadius, cornerRadius, // Bottom right radius in px
        cornerRadius, cornerRadius  // Bottom left radius in px
    )

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
                fm.ascent = (defaultAscent - 2 * padding).toInt()
                fm.descent = (defaultDescent).toInt()
            }
            Element.BlockCode.Type.END -> {
                fm.ascent = (defaultAscent).toInt()
                fm.descent = (defaultDescent + 2 * padding).toInt()
            }
            Element.BlockCode.Type.MIDDLE -> {
                fm.ascent = (defaultAscent).toInt()
                fm.descent = (defaultDescent).toInt()
            }
            Element.BlockCode.Type.SINGLE -> {
                fm.ascent = (defaultAscent - 2 * padding).toInt()
                fm.descent = (defaultDescent + 2 * padding).toInt()
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
                    path.reset()
                    rect.set(
                        0f,
                        top + padding,
                        x + canvas.width,
                        bottom.toFloat()
                    )
                    path.addRoundRect(rect, startBlockRadius, Path.Direction.CW)
                    canvas.drawPath(path, paint)
                }
                Element.BlockCode.Type.END -> {
                    path.reset()
                    rect.set(
                        0f,
                        top.toFloat(),
                        x + canvas.width,
                        bottom - padding
                    )
                    path.addRoundRect(rect, endBlockRadius, Path.Direction.CW)
                    canvas.drawPath(path, paint)
                }
                Element.BlockCode.Type.MIDDLE -> {
                    rect.set(
                        0f,
                        top.toFloat(),
                        x + canvas.width,
                        bottom.toFloat()
                    )
                    canvas.drawRect(rect, paint)
                }
                Element.BlockCode.Type.SINGLE -> {
                    rect.set(
                        0f,
                        top + padding,
                        x + canvas.width,
                        bottom - padding
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
        textSize *= FONT_SCALE

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
