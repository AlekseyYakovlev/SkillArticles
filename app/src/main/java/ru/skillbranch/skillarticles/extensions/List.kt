package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<List<Pair<Int, Int>>> {
    return bounds.fold(mutableListOf<List<Pair<Int, Int>>>()) { group, bound ->
        group.add(
            this.fold(mutableListOf<Pair<Int, Int>>()) { element, range ->
                if (range.first < bound.second && bound.first < range.second) {
                    val first = if (range.first > bound.first) range.first else bound.first
                    val second = if (range.second < bound.second) range.second else bound.second
                    element.add(first to second)
                }
                element
            }.toList()
        )
        group
    }.toList()
}
