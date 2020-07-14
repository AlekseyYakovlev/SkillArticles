package ru.skillbranch.skillarticles.data.remote.res

import ru.skillbranch.skillarticles.data.local.entities.Author
import ru.skillbranch.skillarticles.data.local.entities.Category
import java.util.*

data class ArticleRes (
    val data: ArticleDataRes,
    val counts: ArticleCountsRes,
    val isActive:Boolean = true
)

data class ArticleCountsRes(
    val articleId: String,
    val likes: Int = 0,
    val comments: Int = 0,
    val readDuration: Int = 0,
    val updatedAt:Long
)

data class ArticleDataRes(
    val id: String,
    val date: Date ,
    val author: Author,
    val title: String,
    val description: String,
    val poster: String,
    val category: Category,
    val tags : List<String> = listOf()
)

data class ArticleContentRes(
    val articleId: String,
    val content: String,
    val source: String? = null,
    val shareLink: String,
    val updatedAt: Date = Date()
)