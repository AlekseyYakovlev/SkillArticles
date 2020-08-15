package ru.skillbranch.skillarticles.data.remote.res


import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class ArticleContentRes(
    val articleId: String,
    val updatedAt: Date,
    val content: String,
    val source: String,
    val shareLink: String
)