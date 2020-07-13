package ru.skillbranch.skillarticles.ui.custom

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.core.graphics.toRectF
import androidx.core.view.children
import androidx.core.view.doOnLayout

class ShimmerDrawable private constructor(private val pattern: MutableList<Shape>) : Drawable() {
    private var shimmerAngle: Float = 0f
    private var patternDrawable: Drawable? = null
    private val patternPath = Path()
    private val shimmerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shaderColors = intArrayOf(Color.LTGRAY, Color.DKGRAY, Color.LTGRAY)
    private var shaderWidth: Int? = null
    private val shimmerAnimator = ValueAnimator.ofFloat(1f).apply {
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        duration = 3000
        addUpdateListener { invalidateSelf() }
    }
    private val shaderMatrix = Matrix()
    private var offset = 0f
    private lateinit var boundsF: RectF

    override fun draw(canvas: Canvas) {
        offset = (shaderWidth?.toFloat())?.times(shimmerAnimator.animatedFraction)
            ?: bounds.width().times(shimmerAnimator.animatedFraction)
        shaderMatrix.reset()
        shaderMatrix.setTranslate(offset, 0f)
        shaderMatrix.postRotate(shimmerAngle)
        shimmerPaint.shader.setLocalMatrix(shaderMatrix)
        if (patternDrawable == null) canvas.drawPath(patternPath, shimmerPaint)
        else {
            canvas.saveLayer(boundsF, null)
            patternDrawable?.bounds = bounds
            patternDrawable?.draw(canvas)
            canvas.drawRect(bounds, shimmerPaint)
        }
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        if (bounds?.width() ?: 0 > 0) {
            boundsF = bounds!!.toRectF()
            preparePattern()
            prepareShader()
        }
    }

    override fun setAlpha(alpha: Int) {
        shimmerPaint.alpha = alpha
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        shimmerPaint.colorFilter = colorFilter
    }

    private fun prepareShader() {
        shimmerPaint.shader = LinearGradient(
            0f, 0f, shaderWidth?.toFloat() ?: bounds.width().toFloat(), 0f,
            shaderColors,
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.REPEAT
        )
    }

    private fun preparePattern() {
        patternPath.reset()
        pattern.forEach {
            val (dx, dy) = it.offset
            patternPath.withTranslation(dx, dy) {
                it.drawShape(this)
            }
        }
    }

    fun updatePattern(shapes: List<Shape>) {
        pattern.clear()
        pattern.addAll(shapes)
        preparePattern()
        invalidateSelf()
    }

    fun setPatternDrawable(drawable: Drawable) {
        patternDrawable = drawable
        shimmerPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    fun setShimmerWidth(width: Int?) {
        shaderWidth = width
        prepareShader()
    }

    fun setShimmerAngle(angle: Float) {
        shimmerAngle = angle
    }

    fun setShimmerInterpolator(interpolator: Interpolator?) {
        interpolator ?: return
        shimmerAnimator.interpolator = interpolator
    }

    fun setBaseColor(@ColorInt color: Int?) {
        color ?: return
        shaderColors[0] = color
        shaderColors[2] = color
        prepareShader()
    }

    fun setHighlightColor(@ColorInt color: Int?) {
        color ?: return
        shaderColors[1] = color
        prepareShader()
    }

    fun setShimmerDuration(duration: Long) {
        if (duration == 0L) return
        shimmerAnimator.duration = duration
    }

    fun start() {
        if (shimmerAnimator.isPaused) shimmerAnimator.resume()
        else shimmerAnimator.start()
    }

    fun stop() {
        if (shimmerAnimator.isRunning || shimmerAnimator.isStarted) shimmerAnimator.pause()
    }

    class Builder() {
        private val shapes: MutableList<Shape> = mutableListOf()
        private var shimmerDuration: Long = 0
        private var baseColor: Int? = null
        private var highlightColor: Int? = null
        private var itemCount = 0
        private var shimmerAngle: Float = 0f
        private var shimmerInterpolator: Interpolator? = null
        private var shimmerWidth: Int? = null

        fun setShimmerAngle(angle: Float): Builder {
            shimmerAngle = angle
            return this
        }

        fun setShimmerInterpolator(interpolator: Interpolator): Builder {
            shimmerInterpolator = interpolator
            return this
        }

        fun setShimmerWidth(width: Int): Builder {
            shimmerWidth = width
            return this
        }

        fun setBaseColor(@ColorInt color: Int): Builder {
            baseColor = color
            return this
        }

        fun setHighlightColor(@ColorInt color: Int): Builder {
            highlightColor = color
            return this
        }

        fun setDuration(duration: Long): Builder {
            shimmerDuration = duration
            return this
        }

        fun itemPatternCount(count: Int): Builder {
            itemCount = count
            return this
        }

        fun addShape(shape: Shape): Builder {

            val last = shapes.lastOrNull()
            val dy = last?.let { it.offset.second + it.height }
            shape.addTopOffset(dy ?: 0)

            shapes.add(shape)
            return this
        }

        fun build(): ShimmerDrawable {
            if (itemCount > 0) {
                var topOffset = shapes.lastOrNull()?.let { it.offset.second + it.height } ?: 0
                val itemPattern = shapes.toList() //clone current pattern
                repeat(itemCount.dec()) {
                    itemPattern
                        .map { it.clone() }
                        .map { it.addTopOffset(topOffset); it }
                        .also { shapes.addAll(it) }
                    topOffset = shapes.lastOrNull()!!.let { it.offset.second + it.height }
                }
            }
            return ShimmerDrawable(shapes).apply {
                setShimmerDuration(shimmerDuration)
                setBaseColor(baseColor)
                setHighlightColor(highlightColor)
                setShimmerInterpolator(shimmerInterpolator)
                setShimmerAngle(shimmerAngle)
                setShimmerWidth(shimmerWidth)
            }
        }
    }

    companion object {
        fun fromView(
            view: View,
            rules: ((View) -> Shape)? = null
        ): ShimmerDrawable {
            val drawable = ShimmerDrawable(mutableListOf())

            if (view is ViewGroup) {
                view.doOnLayout {
                    val shapes = view.children
                        .map { v ->
                            rules?.invoke(v) ?: when {
                                v.width == v.height -> Shape.Round(
                                    v.width,
                                    offset = v.left to v.top
                                )
                                else -> Shape.Rectangle(v.width, v.height, offset = v.left to v.top)
                            }
                        }
                        .toList()
                    drawable.updatePattern(shapes)
                }
            } else {
                view.doOnLayout {
                    drawable.updatePattern(
                        listOf(
                            Shape.Rectangle(
                                it.width,
                                it.height,
                                offset = it.top to it.left
                            )
                        )
                    )
                }
            }
            view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    drawable.stop()
                }

                override fun onViewAttachedToWindow(v: View?) {}
            })
            return drawable
        }

        fun fromDrawable(drawable: Drawable): ShimmerDrawable =
            ShimmerDrawable(mutableListOf()).apply {
                setPatternDrawable(drawable)
            }
    }

    sealed class Shape() {
        abstract val width: Int
        abstract val height: Int
        abstract var offset: Pair<Int, Int>

        data class Rectangle(
            override val width: Int,
            override val height: Int,
            override var offset: Pair<Int, Int> = 0 to 0,
            val cornerRadius: Int = 16
        ) : Shape()

        data class Round(
            val size: Int,
            override var offset: Pair<Int, Int> = 0 to 0
        ) : Shape() {
            override val width: Int = size
            override val height: Int = size
        }

        data class TextRow(
            override val width: Int,
            val lines: Int,
            val lineHeight: Int = 14,
            val lineSpace: Int = 8,
            val cornerRadius: Int = 16,
            val rowsPattern: MutableList<List<Float>> = mutableListOf(),
            override var offset: Pair<Int, Int> = 0 to 0
        ) : Shape() {
            override val height: Int = (lineHeight + lineSpace) * lines

            init {
                if (rowsPattern.isEmpty()) {
                    val rnd = rndIterator(3, 7, lines)
                    repeat(lines) { rowsPattern.add(nextRowPattern(rnd.next())) }
                }
            }

            private fun nextRowPattern(segmentCount: Int): List<Float> {
                val tw = width - segmentCount.dec() * lineSpace
                return when (segmentCount) {
                    3 -> listOf(tw / 3f, tw / 6f, tw / 2f)
                    4 -> listOf(tw / 4f, tw / 6f, 5 * tw / 12f, tw / 6f)
                    5 -> listOf(5 * tw / 12f, tw / 12f, tw / 6f, tw / 4f, tw / 12f)
                    6 -> listOf(tw / 12f, tw / 6f, tw / 12f, tw / 3f, tw / 12f, tw / 4f)
                    else -> listOf(tw / 3f, tw / 6f, tw / 2f)
                }
            }

            private fun rndIterator(from: Int, until: Int, capacity: Int): Iterator<Int> {
                return IntArray(until - from) { from + it }
                    .toList()
                    .shuffled()
                    .run { IntArray(capacity) { this[it % size] } }
                    .iterator()
            }
        }

        data class ImagePlaceholder(
            override val width: Int,
            val aspectRatio: Float,
            val borderWidth: Int,
            val cornerRadius: Int = 16,
            override var offset: Pair<Int, Int> = 0 to 0
        ) : Shape() {
            override val height: Int = (width / aspectRatio).toInt()
        }

        fun addTopOffset(top: Int) {
            offset = offset.copy(second = offset.second + top)
        }

        fun drawShape(path: Path) {
            when (this) {
                is Round -> path.addCircle(
                    size / 2f,
                    size / 2f,
                    size / 2f,
                    Path.Direction.CW
                )

                is Rectangle -> path.addRoundRect(
                    RectF(0f, 0f, width.toFloat(), height.toFloat()),
                    cornerRadius.toFloat(),
                    cornerRadius.toFloat(),
                    Path.Direction.CW
                )

                is TextRow -> {
                    var lh = 0f
                    rowsPattern.forEach { row ->
                        row.fold(0f) { o, w ->
                            //o - horizontal offset
                            //w - segment width
                            path.addRoundRect(
                                RectF(o, lh, o + w, lh + lineHeight),
                                cornerRadius.toFloat(),
                                cornerRadius.toFloat(),
                                Path.Direction.CW
                            )
                            o + w + lineSpace
                        }
                        lh += (lineHeight + lineSpace)
                    }
                }

                is ImagePlaceholder -> {
                    val area = RectF(0f, 0f, width.toFloat(), height.toFloat())
                    path.addRoundRect(
                        area,
                        cornerRadius.toFloat(),
                        cornerRadius.toFloat(),
                        Path.Direction.CW
                    )

                    val innerPath = Path()
                    area.inset(borderWidth.toFloat(), borderWidth.toFloat())
                    innerPath.addRect(
                        area,
                        Path.Direction.CW
                    )
                    path.op(innerPath, Path.Op.DIFFERENCE)

                    area.inset(borderWidth.toFloat(), borderWidth.toFloat())
                    val trianglePath = Path()
                    val hStep = area.width() / 4
                    val vStep = area.height() / 4
                    val points = listOf(
                        0f to -2 * vStep,
                        hStep to -vStep,
                        2 * hStep to 2 * vStep,
                        hStep to -vStep,
                        0f to 2 * vStep
                    )

                    trianglePath.moveTo(area.left, area.bottom)
                    points.forEach { trianglePath.rLineTo(it.first, it.second) }
                    trianglePath.close()

                    path.op(trianglePath, Path.Op.UNION)

                    val roundPath = Path()
                    roundPath.addOval(
                        RectF(0f, 0f, 0.15f * area.width(), 0.15f * area.width()),
                        Path.Direction.CW
                    )
                    roundPath.offset(
                        area.right - hStep - 0.15f * area.width() / 2,
                        area.top
                    )

                    path.op(roundPath, Path.Op.UNION)
                }
            }
        }

        fun clone(): Shape {
            return when (this) {
                is Rectangle -> this.copy()
                is Round -> this.copy()
                is TextRow -> this.copy()
                is ImagePlaceholder -> this.copy()
            }
        }

    }

}

private fun Path.withTranslation(x: Int, y: Int, block: Path.() -> Unit) {
    offset(-x.toFloat(), -y.toFloat())
    block.invoke(this)
    offset(x.toFloat(), y.toFloat())
}