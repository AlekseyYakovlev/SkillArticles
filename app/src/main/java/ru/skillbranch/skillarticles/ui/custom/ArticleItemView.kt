package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.shortFormat
import kotlin.math.max

class ArticleItemView constructor(
    context: Context
) : ViewGroup(context, null, 0) {
    private val iv_poster: ImageView
    private val iv_category: ImageView
    private val iv_likes: ImageView
    private val iv_comments: ImageView
    private val iv_bookmark: CheckableImageView
    private val tv_date: TextView
    private val tv_author: TextView
    private val tv_title: TextView
    private val tv_description: TextView
    private val tv_likes_count: TextView
    private val tv_comments_count: TextView
    private val tv_read_duration: TextView

    private val defaultPadding = context.dpToIntPx(16)
    private val defaultSpace = context.dpToIntPx(8)
    private val cornerRadius = context.dpToIntPx(8)
    private val categorySize = context.dpToIntPx(40)
    private val posterSize = context.dpToIntPx(64)
    private val iconSize = context.dpToIntPx(16)
    private val grayColor = context.getColor(R.color.color_gray)
    private val primaryColor = context.attrValue(R.attr.colorPrimary)


    init {
        setPadding(defaultPadding)
        tv_date = TextView(context).apply {
            id = R.id.tv_date
            setTextColor(grayColor)
            textSize = 12f
        }

        addView(tv_date)

        tv_author = TextView(context).apply {
            id = R.id.tv_author
            setTextColor(primaryColor)
            textSize = 12f
        }
        addView(tv_author)

        tv_title = TextView(context).apply {
            id = R.id.tv_title
            setTextColor(context.attrValue(R.attr.colorPrimary))
            textSize = 18f
            setTypeface(typeface, Typeface.BOLD)
        }

        addView(tv_title)

        iv_poster = ImageView(context).apply {
            id = R.id.iv_poster
            layoutParams = LayoutParams(posterSize, posterSize)
        }

        addView(iv_poster)

        iv_category = ImageView(context).apply {
            id = R.id.iv_category
            layoutParams = LayoutParams(categorySize, categorySize)
        }
        addView(iv_category)

        tv_description = TextView(context).apply {
            id = R.id.tv_description
            setTextColor(grayColor)
            textSize = 14f
        }
        addView(tv_description)

        iv_likes = ImageView(context).apply {
            id = R.id.iv_likes
            layoutParams = LayoutParams(iconSize, iconSize)
            imageTintList = ColorStateList.valueOf(grayColor)
            setImageResource(R.drawable.ic_favorite_black_24dp)
        }

        addView(iv_likes)

        tv_likes_count = TextView(context).apply {
            setTextColor(grayColor)
            textSize = 12f
        }
        addView(tv_likes_count)


        iv_comments = ImageView(context).apply {
            imageTintList = ColorStateList.valueOf(grayColor)
            setImageResource(R.drawable.ic_insert_comment_black_24dp)
        }

        addView(iv_comments)

        tv_comments_count = TextView(context).apply {
            id = R.id.tv_comments_count
            setTextColor(grayColor)
            textSize = 12f
        }
        addView(tv_comments_count)

        tv_read_duration = TextView(context).apply {
            id = R.id.tv_read_duration
            setTextColor(grayColor)
            textSize = 12f
        }
        addView(tv_read_duration)

        iv_bookmark = CheckableImageView(context).apply {
            id = R.id.iv_bookmark
            imageTintList = ColorStateList.valueOf(grayColor)
            setImageResource(R.drawable.bookmark_states)
        }

        addView(iv_bookmark)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = paddingTop
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        measureChild(tv_date, widthMeasureSpec, heightMeasureSpec)
        tv_author.maxWidth = width - (tv_date.measuredWidth + 3 * defaultPadding)
        measureChild(tv_author, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tv_author.measuredHeight

        //title row
        val rh = posterSize + categorySize / 2
        tv_title.maxWidth = width - (rh + 2 * paddingLeft + defaultSpace)
        measureChild(tv_title, widthMeasureSpec, heightMeasureSpec)
        usedHeight += max(tv_title.measuredHeight, rh) + 2 * defaultSpace

        //description row
        measureChild(tv_description, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tv_description.measuredHeight + defaultSpace

        //icon row
        measureChild(tv_likes_count, widthMeasureSpec, heightMeasureSpec)
        measureChild(tv_comments_count, widthMeasureSpec, heightMeasureSpec)
        measureChild(tv_read_duration, widthMeasureSpec, heightMeasureSpec)

        usedHeight += iconSize + paddingBottom
        setMeasuredDimension(width, usedHeight)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = paddingTop
        val bodyWidth = right - left - paddingLeft - paddingRight
        var left = paddingLeft

        tv_date.layout(
            left,
            usedHeight,
            left + tv_date.measuredWidth,
            usedHeight + tv_date.measuredHeight
        )
        left = tv_date.right + defaultPadding
        tv_author.layout(
            left,
            usedHeight,
            left + tv_author.measuredWidth,
            usedHeight + tv_author.measuredHeight
        )
        usedHeight += tv_author.measuredHeight + defaultSpace
        left = paddingLeft

        val rh = posterSize + categorySize / 2
        val leftTop = if (rh > tv_title.measuredHeight) (rh - tv_title.measuredHeight) / 2 else 0
        val rightTop = if (rh < tv_title.measuredHeight) (tv_title.measuredHeight - rh) / 2 else 0

        tv_title.layout(
            left,
            usedHeight + leftTop,
            left + tv_title.measuredWidth,
            usedHeight + leftTop + tv_title.measuredHeight
        )
        iv_poster.layout(
            left + bodyWidth - posterSize,
            usedHeight + rightTop,
            left + bodyWidth,
            usedHeight + rightTop + posterSize
        )
        iv_category.layout(
            iv_poster.left - categorySize / 2,
            iv_poster.bottom - categorySize / 2,
            iv_poster.left + categorySize / 2,
            iv_poster.bottom + categorySize / 2
        )
        usedHeight += if (rh > tv_title.measuredHeight) rh else tv_title.measuredHeight
        usedHeight += defaultSpace

        tv_description.layout(
            left,
            usedHeight,
            left + bodyWidth,
            usedHeight + tv_description.measuredHeight
        )
        usedHeight += tv_description.measuredHeight + defaultSpace

        val fontDiff = iconSize - tv_likes_count.measuredHeight
        iv_likes.layout(
            left,
            usedHeight - fontDiff,
            left + iconSize,
            usedHeight + iconSize - fontDiff
        )

        left = iv_likes.right + defaultSpace
        tv_likes_count.layout(
            left,
            usedHeight,
            left + tv_likes_count.measuredWidth,
            usedHeight + tv_likes_count.measuredHeight
        )
        left = tv_likes_count.right + defaultPadding

        iv_comments.layout(
            left,
            usedHeight - fontDiff,
            left + iconSize,
            usedHeight + iconSize - fontDiff
        )
        left = iv_comments.right + defaultSpace
        tv_comments_count.layout(
            left,
            usedHeight,
            left + tv_comments_count.measuredWidth,
            usedHeight + tv_comments_count.measuredHeight
        )
        left = tv_comments_count.right + defaultPadding
        tv_read_duration.layout(
            left,
            usedHeight,
            left + tv_read_duration.measuredWidth,
            usedHeight + tv_read_duration.measuredHeight
        )

        left = defaultPadding
        iv_bookmark.layout(
            left + bodyWidth - iconSize,
            usedHeight - fontDiff,
            left + bodyWidth,
            usedHeight + iconSize - fontDiff
        )
    }

    fun bind(item: ArticleItemData, listener: (ArticleItemData, Boolean) -> Unit) {

        tv_date.text = item.date.shortFormat()
        tv_author.text = item.author
        tv_title.text = item.title

        Glide.with(context)
            .load(item.poster)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(posterSize)
            .into(iv_poster)

        Glide.with(context)
            .load(item.categoryIcon)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(categorySize)
            .into(iv_category)

        tv_description.text = item.description
        tv_likes_count.text = "${item.likeCount}"
        tv_comments_count.text = "${item.commentCount}"
        tv_read_duration.text = "${item.readDuration} min read"
        iv_bookmark.isChecked = item.isBookmark
        iv_bookmark.setOnClickListener { listener.invoke(item, true) }
        this.setOnClickListener { listener.invoke(item, false) }
    }
}