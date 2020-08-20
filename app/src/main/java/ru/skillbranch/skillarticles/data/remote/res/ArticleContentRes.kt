package ru.skillbranch.skillarticles.data.remote.res

import java.util.*

//@JsonClass(generateAdapter = true)
data class ArticleContentRes(
    val articleId: String,
    val content: String,
    val source: String? = null,
    val shareLink: String,
    val updatedAt: Date = Date()
)