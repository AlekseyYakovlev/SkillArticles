package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.text.Spannable
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.core.animation.doOnEnd
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.dpToPx
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.math.hypot


@SuppressLint("ViewConstructor")
class MarkdownImageView private constructor(
    context: Context,
    fontSize: Float
) : ViewGroup(context, null, 0), IMarkdownView {

    companion object {
        const val TITLE_FONT_SIZE_RATIO = 0.75f
    }

    override var fontSize: Float = fontSize
        set(value) {
            tv_title.textSize = value * TITLE_FONT_SIZE_RATIO
            tv_alt?.textSize = value
            field = value
        }

    override val spannableContent: Spannable
        get() = tv_title.text as Spannable

    //views
    private lateinit var imageUrl: String
    private lateinit var imageTitle: CharSequence

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val iv_image: ImageView

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val tv_title: MarkdownTextView

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var tv_alt: TextView? = null

    @Px
    private val titleTopMargin: Int = context.dpToIntPx(8)

    @Px
    private val titlePadding: Int = context.dpToIntPx(56)

    @Px
    private val cornerRadius: Float = context.dpToPx(4)

    @ColorInt
    private val colorSurface: Int = context.attrValue(R.attr.colorSurface)

    @ColorInt
    private val colorOnSurface: Int = context.attrValue(R.attr.colorOnSurface)

    @ColorInt
    private val colorOnBackground: Int = context.attrValue(R.attr.colorOnBackground)

    @ColorInt
    private var lineColor: Int = context.getColor(R.color.color_divider)

    //for draw object allocation
    private var linePositionY: Float = 0f
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = lineColor
        strokeWidth = 0f
    }

    //private var isAltTextOpen = false
    private var aspectRatio = 0f

    init {
        isSaveEnabled = true

        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        iv_image = ImageView(context).apply {
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(
                        Rect(0, 0, view.measuredWidth, view.measuredHeight),
                        cornerRadius
                    )
                }
            }
            clipToOutline = true
        }
        addView(iv_image)

        tv_title = MarkdownTextView(context, fontSize * TITLE_FONT_SIZE_RATIO)
            .apply {
                setTextColor(colorOnBackground)
                gravity = Gravity.CENTER
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                setPaddingOptionally(left = titlePadding, right = titlePadding)
            }
        addView(tv_title)
    }

    constructor(
        context: Context,
        fontSize: Float,
        url: String,
        title: CharSequence,
        alt: String?
    ) : this(context, fontSize) {
        imageUrl = url
        imageTitle = title

        tv_title.setText(title, TextView.BufferType.SPANNABLE)

        alt?.let {
            tv_alt = TextView(context).apply {
                text = it
                setTextColor(colorOnSurface)
                setBackgroundColor(ColorUtils.setAlphaComponent(colorOnSurface, 160))
                gravity = Gravity.CENTER
                textSize = fontSize
                setPadding(titleTopMargin)
                isVisible = false
            }
            addView(tv_alt)

            iv_image.setOnClickListener {
                if (tv_alt?.isVisible == true) animateHideAlt()
                else animateShowAlt()
                //isAltTextOpen = !isAltTextOpen
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Glide
            .with(context)
            .load(imageUrl)
            .transform(AspectRatioResizeTransform())
            .into(iv_image)
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = 0
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        //create measureSpec for children EXACTLY
        //all children width == parent width (constraint parent width)
        val wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)

        if (aspectRatio != 0f) {
            //restore width/height by aspectRatio
            val hms =
                MeasureSpec.makeMeasureSpec((width / aspectRatio).toInt(), MeasureSpec.EXACTLY)
            iv_image.measure(wms, hms)
        } else iv_image.measure(wms, heightMeasureSpec)

        tv_title.measure(wms, heightMeasureSpec)
        tv_alt?.measure(wms, heightMeasureSpec)

        usedHeight += iv_image.measuredHeight
        usedHeight += titleTopMargin
        linePositionY = usedHeight + tv_title.measuredHeight / 2f
        usedHeight += tv_title.measuredHeight

        setMeasuredDimension(width, usedHeight)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = 0
        val bodyWidth = r - l - paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth

        iv_image.layout(
            left,
            usedHeight,
            right,
            usedHeight + iv_image.measuredHeight
        )

        usedHeight += iv_image.measuredHeight + titleTopMargin

        tv_title.layout(
            left,
            usedHeight,
            right,
            usedHeight + tv_title.measuredHeight
        )

        tv_alt?.layout(
            left,
            iv_image.measuredHeight - (tv_alt?.measuredHeight ?: 0),
            right,
            iv_image.measuredHeight
        )
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(
            0f,
            linePositionY,
            titlePadding.toFloat(),
            linePositionY,
            linePaint
        )

        canvas.drawLine(
            canvas.width - titlePadding.toFloat(),
            linePositionY,
            canvas.width.toFloat(),
            linePositionY,
            linePaint
        )
    }

    private fun animateShowAlt() {
        tv_alt?.isVisible = true
        val endRadius = hypot(tv_alt?.width?.toFloat() ?: 0f, tv_alt?.height?.toFloat() ?: 0f)
        val va = ViewAnimationUtils.createCircularReveal(
            tv_alt,
            tv_alt?.width ?: 0,
            tv_alt?.height ?: 0,
            0f,
            endRadius
        )
        va.start()
    }

    private fun animateHideAlt() {
        val endRadius = hypot(tv_alt?.width?.toFloat() ?: 0f, tv_alt?.height?.toFloat() ?: 0f)
        val va = ViewAnimationUtils.createCircularReveal(
            tv_alt,
            tv_alt?.width ?: 0,
            tv_alt?.height ?: 0,
            endRadius,
            0f
        )
        va.doOnEnd { tv_alt?.isVisible = false }
        va.start()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isAltTextOpen = tv_alt?.isVisible == true
        savedState.aspectRatio = (iv_image.width.toFloat() / iv_image.height)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            aspectRatio = state.aspectRatio
            tv_alt?.isVisible = state.isAltTextOpen
        }
    }

    private class SavedState : BaseSavedState, Parcelable {
        var isAltTextOpen = false
        var aspectRatio = 0f

        constructor(superState: Parcelable?) : super(superState)

        constructor(src: Parcel) : super(src) {
            isAltTextOpen = src.readInt() == 1
            aspectRatio = src.readFloat()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(if (isAltTextOpen) 1 else 0)
            dest.writeFloat(aspectRatio)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}

class AspectRatioResizeTransform : BitmapTransformation() {
    private val ID =
        "ru.skillbranch.skillarticles.glide.AspectRatioResizeTransform" //any unique string
    private val ID_BYTES = ID.toByteArray(Charset.forName("UTF-8"))
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val originalWidth = toTransform.width
        val originalHeight = toTransform.height
        val aspectRatio = originalWidth.toFloat() / originalHeight
        return Bitmap.createScaledBitmap(
            toTransform,
            outWidth,
            (outWidth / aspectRatio).toInt(),
            true
        )
    }

    override fun equals(other: Any?): Boolean = other is AspectRatioResizeTransform

    override fun hashCode(): Int = ID.hashCode()
}