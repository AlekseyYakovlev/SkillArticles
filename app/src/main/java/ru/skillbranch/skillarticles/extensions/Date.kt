package ru.skillbranch.skillarticles.extensions

import android.content.Context
import ru.skillbranch.skillarticles.R
import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR


fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.add(value: Int, unit: TimeUnits = TimeUnits.SECOND): Date {
    time += when (unit) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    return this
}

fun Date.shortFormat(): String {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    return this.format(pattern)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2
}

fun Date.humanizeDiff(context: Context, date: Date = Date()): String {
    var diff = date.time - this.time
    val pattern =
        if (diff > 0) context.resources.getString(R.string.comment_item_view__date_ago) //"%s назад"
        else {
            diff = -diff
            context.resources.getString(R.string.comment_item_view__date_future) //"через %s"
        }

    val seconds = (diff / 1000).toInt()
    val minutes = (seconds / 60)
    val hours = (minutes / 60)
    val days = (hours / 24)
    val years = (days / 365)

    return when (diff) {
        in 0L..1 * SECOND -> "just now"
        in 1 * SECOND..60 * SECOND -> pattern.format(
            context.resources.getQuantityString(
                R.plurals.comment_item_view__time_second,
                seconds,
                seconds
            )
        )
        in 60 * SECOND..60 * MINUTE -> pattern.format(
            context.resources.getQuantityString(
                R.plurals.comment_item_view__time_minute,
                minutes,
                minutes
            )
        )
        in 60 * MINUTE..24 * HOUR -> pattern.format(
            context.resources.getQuantityString(
                R.plurals.comment_item_view__time_hour,
                hours,
                hours
            )
        )
        in 24 * HOUR..365 * DAY -> pattern.format(
            context.resources.getQuantityString(
                R.plurals.comment_item_view__time_day,
                days,
                days
            )
        )
        else -> pattern.format(
            context.resources.getQuantityString(
                R.plurals.comment_item_view__time_year,
                years,
                years
            )
        )
    }
}

enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY,
}