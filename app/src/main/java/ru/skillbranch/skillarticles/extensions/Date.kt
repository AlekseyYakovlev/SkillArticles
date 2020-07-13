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

fun Date.add(value: Int, unit: TimeUnits = TimeUnits.SECOND): Date {
    time += when (unit) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    return this
}

fun Date.shortFormat(): String? {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    return this.format(pattern)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val diff = date.time - this.time

    return when (Locale.getDefault()) {
        Locale("ru") -> humanizeDiffRu(diff)
        else -> humanizeDiffEng(diff)
    }
}

private fun humanizeDiffRu(timeDiff: Long): String {
    var diff = timeDiff
    var isInFuture = false
    val pattern =
        if (diff > 0) "%s назад"
        else {
            isInFuture = true
            diff = -diff
            "через %s"
        }

    return when (diff) {
        in (0..SECOND) ->
            "только что"
        in (SECOND..45 * SECOND) ->
            pattern.format("несколько секунд")
        in (45 * SECOND..75 * SECOND) ->
            pattern.format("минуту")
        in (75 * SECOND..45 * MINUTE) ->
            pattern.format(TimeUnits.MINUTE.pluralRu((diff / MINUTE).toInt()))
        in (45 * MINUTE..75 * MINUTE) ->
            pattern.format("час")
        in (75 * MINUTE..22 * HOUR) ->
            pattern.format(TimeUnits.HOUR.pluralRu((diff / HOUR).toInt()))
        in (22 * HOUR..26 * HOUR) ->
            pattern.format("день")
        in (26 * HOUR..360 * DAY) ->
            pattern.format(TimeUnits.DAY.pluralRu((diff / DAY).toInt()))
        in (360 * DAY..Long.MAX_VALUE) ->
            if (isInFuture) pattern.format("более чем год")
            else pattern.format("более года")
        else -> "Ошибка!"
    }
}

private fun humanizeDiffEng(timeDiff: Long): String {
    var diff = timeDiff
    val seconds = (diff / 1000)
    val minutes = (seconds / 60)
    val hours = (minutes / 60)
    val days = (hours / 24)

    var isInFuture = false
    val pattern =
        if (diff > 0) "%s ago"
        else {
            isInFuture = true
            diff = -diff
            "in %s"
        }

    return when (diff) {
        in 0L..1 * SECOND -> "just now"
        in 1 * SECOND..45 * SECOND -> pattern.format("a few seconds")
        in 45 * SECOND..75 * SECOND -> pattern.format("a minute")
        in 75 * SECOND..45 * MINUTE -> pattern.format("$minutes minutes")
        in 45 * MINUTE..75 * MINUTE -> pattern.format("an hour")
        in 75 * MINUTE..22 * HOUR -> pattern.format("$hours hour")
        in 22 * HOUR..26 * HOUR -> pattern.format("one day")
        in 26 * HOUR..360 * DAY -> pattern.format("$days days")
        else -> pattern.format("more than a year")
    }
}


enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY;

    fun pluralRu(value: Int): String {

        val rangeType = when {
            value in (5..20) || value % 10 in (5..10) -> 3
            value % 10 == 1 -> 1
            value % 10 in (2..4) -> 2
            else -> 3
        }

        return when (this) {
            SECOND -> when (rangeType) {
                1 -> "$value секунду"
                2 -> "$value секунды"
                3 -> "$value секунд"
                else -> "er1ошибка"
            }
            MINUTE -> when (rangeType) {
                1 -> "$value минуту"
                2 -> "$value минуты"
                3 -> "$value минут"
                else -> "er2ошибка"
            }
            HOUR -> when (rangeType) {
                1 -> "$value час"
                2 -> "$value часа"
                3 -> "$value часов"
                else -> "er3ошибка"
            }
            DAY -> when (rangeType) {
                1 -> "$value день"
                2 -> "$value дня"
                3 -> "$value дней"
                else -> "er4ошибка"
            }
        }
    }
}