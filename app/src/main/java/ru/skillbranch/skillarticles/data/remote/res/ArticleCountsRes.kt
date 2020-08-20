package ru.skillbranch.skillarticles.data.remote.res

import java.util.*

//@JsonClass(generateAdapter = true)
data class ArticleCountsRes(
    val articleId: String,
    val likes: Int = 0,
    val comments: Int = 0,
    val readDuration: Int = 0,
    val updatedAt: Date
)