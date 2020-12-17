package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.local.entities.Author
import ru.skillbranch.skillarticles.data.local.entities.Category
import ru.skillbranch.skillarticles.data.remote.res.ArticleDataRes
import ru.skillbranch.skillarticles.data.remote.res.AuthorRes
import ru.skillbranch.skillarticles.data.remote.res.CategoryRes
import java.util.*

fun ArticleDataRes.toArticle(): Article = Article(
    id = id,
    title = title,
    description = description,
    author = author.toAuthor(),
    categoryId = category.id,
    poster = poster,
    date = date,
    updatedAt = Date()
)

fun AuthorRes.toAuthor(): Author = Author(
    userId = id,
    avatar = avatar,
    name = name
)

fun CategoryRes.toCategory() = Category(
    categoryId = id,
    title = title,
    icon = icon
)