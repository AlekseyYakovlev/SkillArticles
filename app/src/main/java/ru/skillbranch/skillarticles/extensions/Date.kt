package ru.skillbranch.skillarticles.extensions

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

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time;

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val diff = date.time - this.time;
    val seconds = (diff / 1000)
    val minutes = (seconds / 60)
    val hours = (minutes / 60)
    val days = (hours / 24)

    return when (diff) {
        in 0L..1 * SECOND -> "just now"
        in 1 * SECOND..45 * SECOND -> "a few seconds ago"
        in 45 * SECOND..75 * SECOND -> "a minute ago"
        in 75 * SECOND..45 * MINUTE -> "$minutes minutes ago"
        in 45 * MINUTE..75 * MINUTE -> "hour ago"
        in 75 * MINUTE..22 * HOUR -> "$hours hour ago"
        in 22 * HOUR..26 * HOUR -> "one day ago"
        in 26 * HOUR..360 * DAY -> "$days days ago"
        else -> "more than a year ago"
    }
}

fun Date.shortFormat(): String {
    val pattern  = if(this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern,Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.isSameDay(date:Date):Boolean{
    val day1 = this.time/DAY
    val day2 = date.time/DAY
    return day1 == day2
}

enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY,
}