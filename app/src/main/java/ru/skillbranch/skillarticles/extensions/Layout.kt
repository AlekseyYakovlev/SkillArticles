package ru.skillbranch.skillarticles.extensions

import android.text.Layout

/**
 * Get the line's height
 */
fun Layout.getLineHeight(line: Int): Int =
    getLineTop(line.inc()) - getLineTop(line)

/**
 * Returns the top of the Layout after removing extra padding applied by Layout
 */
fun Layout.getLineTopWithoutPadding(line: Int): Int {
    var lineTop = getLineTop(line)
    if (line == 0) {
        lineTop -= topPadding
    }
    return lineTop
}

/**
 * Returns the bottom of the Layout after removing extra padding applied by Layout
 */
fun Layout.getLineBottomWithoutPadding(line: Int): Int {
    var lineBottom = getLineBottomWithoutSpacing(line)
    if (line == lineCount.dec()) {
        lineBottom -= bottomPadding
    }
    return lineBottom
}

/**
 * Get the line bottom discarding the line spacing added
 */
fun Layout.getLineBottomWithoutSpacing(line: Int): Int {
    val lineBottom = getLineBottom(line)
    val isLastLine = line == lineCount.dec()
    val hasLineSpacing = spacingAdd != 0f

    val nextLineIsLast = line == lineCount - 2

    val onlyWhitespaceIsAfter = if (nextLineIsLast) {
        val start = getLineStart(line + 1)
        val lastVisible = getLineVisibleEnd(line + 1)
        start == lastVisible
    } else false

    return if (!hasLineSpacing || isLastLine || onlyWhitespaceIsAfter) {
        lineBottom
    } else {
        lineBottom - spacingAdd.toInt()
    }
}