package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Layout
import android.text.Spanned
import androidx.annotation.VisibleForTesting
import androidx.core.graphics.ColorUtils
import androidx.core.text.getSpans
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.*
import ru.skillbranch.skillarticles.ui.custom.spans.HeaderSpan
import ru.skillbranch.skillarticles.ui.custom.spans.SearchFocusSpan
import ru.skillbranch.skillarticles.ui.custom.spans.SearchSpan

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class SearchBgHelper constructor(
    context: Context,
    private val focusListener: ((Int, Int) -> Unit)? = null,
    mockDrawable: Drawable? = null //for mock drawable
) {
    constructor(context: Context, focusListener: ((Int, Int) -> Unit)) : this(
        context,
        focusListener,
        null
    )

    private val padding: Int = context.dpToIntPx(4)
    private val borderWidth: Int = context.dpToIntPx(1)
    private val radius: Float = context.dpToPx(8)

    private val secondaryColor: Int = context.attrValue(R.attr.colorSecondary)
    private val alphaColor: Int = ColorUtils.setAlphaComponent(secondaryColor, 160)

    val drawable: Drawable by lazy {
        mockDrawable ?: GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = FloatArray(8).apply { fill(radius, 0, size) }
            color = ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth, secondaryColor)
        }
    }

    private val drawableLeft: Drawable by lazy {
        mockDrawable ?: GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(
                radius, radius,  // Top left radius in px
                0f, 0f,   // Top right radius in px
                0f, 0f,     // Bottom right radius in px
                radius, radius      // Bottom left radius in px
            )
            color = ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth, secondaryColor)
        }
    }

    private val drawableMiddle: Drawable by lazy {
        mockDrawable ?: GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            color = ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth, secondaryColor)
        }
    }

    private val drawableRight: Drawable by lazy {
        mockDrawable ?: GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(
                0f, 0f,  // Top left radius in px
                radius, radius,   // Top right radius in px
                radius, radius,     // Bottom right radius in px
                0f, 0f      // Bottom left radius in px
            )
            color = ColorStateList.valueOf(alphaColor)
            setStroke(borderWidth, secondaryColor)
        }
    }

    private lateinit var render: SearchBgRender
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val singleLineRender: SearchBgRender =
        SingleLineRender(
            padding, drawable
        )

    private val multiLineRender: SearchBgRender =
        MultiLineRender(
            padding,
            drawableLeft,
            drawableMiddle,
            drawableRight
        )



    private lateinit var spans: Array<out SearchSpan>
    private lateinit var headerSpans: Array<out HeaderSpan>

    private var spanStart = 0
    private var spanEnd = 0
    private var startLine = 0
    private var endLine = 0
    private var startOffset = 0
    private var endOffset = 0
    private var topExtraPadding = 0
    private var bottomExtraPadding = 0

    fun draw(canvas: Canvas, text: Spanned, layout: Layout) {
//        println(drawable)
        spans = text.getSpans()
        spans.forEach {
            spanStart = text.getSpanStart(it)
            spanEnd = text.getSpanEnd(it)
            startLine = layout.getLineForOffset(spanStart)
            endLine = layout.getLineForOffset(spanEnd)

            if (it is SearchFocusSpan) {
                //if search focus invoke listener for focus
                focusListener?.invoke(layout.getLineTop(startLine), layout.getLineBottom(startLine))
            }

            headerSpans = text.getSpans(spanStart, spanEnd, HeaderSpan::class.java)

            topExtraPadding = 0
            bottomExtraPadding = 0

            if (headerSpans.isNotEmpty()) {
                topExtraPadding =
                    if (spanStart in headerSpans[0].firstLineBounds
                        || spanEnd in headerSpans[0].firstLineBounds
                    ) headerSpans[0].topExtraPadding else 0

                bottomExtraPadding =
                    if (spanStart in headerSpans[0].lastLineBounds
                        || spanEnd in headerSpans[0].lastLineBounds
                    ) headerSpans[0].bottomExtraPadding else 0
            }


            startOffset = layout.getPrimaryHorizontal(spanStart).toInt()
            endOffset = layout.getPrimaryHorizontal(spanEnd).toInt()
            render = if (startLine == endLine) singleLineRender else multiLineRender

            render.draw(
                canvas,
                layout,
                startLine,
                endLine,
                startOffset,
                endOffset,
                topExtraPadding,
                bottomExtraPadding
            )
        }
    }
}


abstract class SearchBgRender(
    val padding: Int
) {
    abstract fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        topExtraPadding: Int = 0,
        bottomExtraPadding: Int = 0
    )

    fun getLineTop(layout: Layout, line: Int): Int {
        return layout.getLineTopWithoutPadding(line)
    }

    fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottomWithoutPadding(line)
    }
}

class SingleLineRender(
    padding: Int,
    val drawable: Drawable
) : SearchBgRender(padding) {

    private var lineTop: Int = 0
    private var lineBottom: Int = 0

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        topExtraPadding: Int,
        bottomExtraPadding: Int
    ) {
        lineTop = getLineTop(layout, startLine) + topExtraPadding
        lineBottom = getLineBottom(layout, startLine) - bottomExtraPadding
        drawable.setBounds(startOffset - padding, lineTop, endOffset + padding, lineBottom)
        drawable.draw(canvas)
    }

}

class MultiLineRender(
    padding: Int,
    private val drawableLeft: Drawable,
    private val drawableMiddle: Drawable,
    private val drawableRight: Drawable
) : SearchBgRender(padding) {
    private var lineTop: Int = 0
    private var lineBottom: Int = 0
    private var lineEndOffset: Int = 0
    private var lineStartOffset: Int = 0

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        topExtraPadding: Int,
        bottomExtraPadding: Int
    ) {
        //draw first line
        val lr = layout.getLineRight(startLine)
        val ll = layout.getLineLeft(startLine)
        lineEndOffset = (layout.getLineRight(startLine) + padding).toInt()
        lineTop = getLineTop(layout, startLine) + topExtraPadding
        lineBottom = getLineBottom(layout, startLine)
        drawStart(canvas, startOffset - padding, lineTop, lineEndOffset, lineBottom)

        //draw middle line
        for (line in startLine.inc() until endLine) {
            lineTop = getLineTop(layout, line)
            lineBottom = getLineBottom(layout, line)
            val l = line
            val ll = layout.getLineLeft(line)
            val lr = layout.getLineRight(line)
            drawableMiddle.setBounds(
                layout.getLineLeft(line).toInt() - padding,
                lineTop,
                layout.getLineRight(line).toInt() + padding,
                lineBottom
            )
            drawableMiddle.draw(canvas)
        }


        //draw last line
        val lle = layout.getLineLeft(endLine)
        lineStartOffset = (layout.getLineLeft(endLine) - padding).toInt()
        lineTop = getLineTop(layout, endLine)
        lineBottom = getLineBottom(layout, endLine) - bottomExtraPadding
        drawEnd(canvas, lineStartOffset, lineTop, endOffset + padding, lineBottom)
    }

    private fun drawStart(
        canvas: Canvas,
        start: Int,
        top: Int,
        end: Int,
        bottom: Int
    ) {
        drawableLeft.setBounds(start, top, end, bottom)
        drawableLeft.draw(canvas)
    }

    private fun drawEnd(
        canvas: Canvas,
        start: Int,
        top: Int,
        end: Int,
        bottom: Int
    ) {
        drawableRight.setBounds(start, top, end, bottom)
        drawableRight.draw(canvas)
    }

}