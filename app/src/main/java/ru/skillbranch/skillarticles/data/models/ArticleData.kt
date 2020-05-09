package ru.skillbranch.skillarticles.data.models

import java.util.*

data class ArticleData(
    val id: String,
    val shareLink: String? = null,
    val title: String? = null,
    val category: String? = null,
    val categoryIcon: String? = null,
    val date: Date,
    val author: User,
    val poster: String? = null,
    val content: List<Any> = emptyList(),
    val commentCount: Int = 0,
    val likeCount: Int = 0,
    val readDuration: Int = 0
)