package ru.skillbranch.skillarticles.data.remote.res

//@JsonClass(generateAdapter = true)
data class ArticleRes(
    val data: ArticleDataRes,
    val counts: ArticleCountsRes,
    val isActive: Boolean
)