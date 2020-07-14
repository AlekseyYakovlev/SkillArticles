package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.ArticleCounts
import ru.skillbranch.skillarticles.data.remote.res.ArticleCountsRes

fun ArticleCountsRes.toArticleCounts(): ArticleCounts {
    return ArticleCounts(
        articleId = articleId,
        likes = likes,
        comments = comments,
        readDuration = readDuration
    )
}