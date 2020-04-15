package ru.skillbranch.skillarticles.extensions


//fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<List<Pair<Int, Int>>> {
//    return bounds.fold(mutableListOf<List<Pair<Int, Int>>>()) { acc, bound ->
//        val element = mutableListOf<Pair<Int, Int>>()
//        this.forEach {
//            if (it.first < bound.second && bound.first < it.second) {
//                val first = if (it.first > bound.first) it.first else bound.first
//                val second = if (it.second < bound.second) it.second else bound.second
//                element.add(first to second)
//            }
//        }
//        acc.add(element.toList())
//        acc
//    }.toList()
//}

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
