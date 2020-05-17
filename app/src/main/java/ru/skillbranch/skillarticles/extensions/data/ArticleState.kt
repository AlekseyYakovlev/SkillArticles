package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.models.ArticlePersonalInfo
import ru.skillbranch.skillarticles.viewmodels.article.ArticleState

fun ArticleState.toAppSettings() : AppSettings {
    return AppSettings(isDarkMode,isBigText)
}

fun ArticleState.toArticlePersonalInfo(): ArticlePersonalInfo {
    return ArticlePersonalInfo(isLike, isBookmark)
}