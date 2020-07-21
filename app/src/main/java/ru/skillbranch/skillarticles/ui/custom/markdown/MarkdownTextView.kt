package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.withTranslation
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx

@SuppressLint("ViewConstructor")
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class MarkdownTextView constructor(
    context: Context,
    fontSize: Float,
    mockHelper: SearchBgHelper? = null //for mock
) : AppCompatTextView(context, null, 0), IMarkdownView {

    constructor(context: Context, fontSize: Float) : this(context, fontSize, null)

    override var fontSize: Float = fontSize
        set(value) {
            textSize = value
            field = value
        }

    override val spannableContent: Spannable
        get() = text as Spannable

    private val color = context.attrValue(R.attr.colorOnBackground) //colorOnBackground
    private val focusRect = Rect()

    private val searchBgHelper: SearchBgHelper

    init {
        searchBgHelper = mockHelper ?: SearchBgHelper(context) { top, bottom ->
            focusRect.set(0, top - context.dpToIntPx(56), width, bottom + context.dpToIntPx(56))
            //show rect on view with animation
            requestRectangleOnScreen(focusRect, false)
        }
        setTextColor(color)
        textSize = fontSize
        movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDraw(canvas: Canvas) {
        if (text is Spannable && layout != null) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                searchBgHelper.draw(canvas, text as Spanned, layout)
            }
        }
        super.onDraw(canvas)
    }
}