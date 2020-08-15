package ru.skillbranch.skillarticles.data.remote.res


import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class ArticleCountsRes(
    val articleId: String,
    val likes: Int,
    val comments: Int,
    val readDuration: Int,
    val updatedAt: Date
)