package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(
    substr: String,
    ignoreCase: Boolean = true
): List<Int> {
    val result = mutableListOf<Int>()
    if (!this.isNullOrEmpty() && substr.isNotEmpty()) {
        var index = 0
        while (index > -1) {
            index = indexOf(substr, index, ignoreCase)
            if (index > -1) {
                result.add(index)
                index += substr.length
            }
        }
    }
    return result
}