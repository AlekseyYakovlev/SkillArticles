package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.remote.res.ArticleDataRes
import java.util.*

fun ArticleDataRes.toArticle(): Article = Article(
    id = id,
    title = title,
    description = description,
    author = author,
    categoryId = category.categoryId,
    poster = poster,
    date = date,
    updatedAt = Date()
)