package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.remote.res.ArticleContentRes

fun ArticleData.toArticleContentRes() : ArticleContentRes = ArticleContentRes(
    articleId = id,
    content = content,
    source = source,
    shareLink = shareLink)