package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {

    if (this.isNullOrBlank() || substr.isBlank()) return emptyList()

    val regex = if (ignoreCase) substr.toRegex(RegexOption.IGNORE_CASE) else substr.toRegex()

    return regex.findAll(this, 0)
        .map { it.range.first }
        .toList()
}