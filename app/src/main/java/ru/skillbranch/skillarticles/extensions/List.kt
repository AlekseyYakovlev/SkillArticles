package ru.skillbranch.skillarticles.extensions

//fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<List<Pair<Int, Int>>> =
//    fold(mutableListOf<List<Pair<Int, Int>>>()){acc, pair ->
//
//    }




fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<List<Pair<Int, Int>>>{
    return bounds.fold(mutableListOf<List<Pair<Int, Int>>>()){acc, bound ->
        val element=mutableListOf<Pair<Int, Int>>()
        this.forEach {
            if (it.first < bound.second && bound.first < it.second) {
                val first = if (it.first > bound.first) it.first else bound.first
                val second = if (it.second < bound.second) it.second else bound.second
                element.add(first to second)
            }
        }
//        if (element.isNotEmpty())
//        element.add(77 to 77)
            acc.add(element.toList())

        acc
    }.toList()
}