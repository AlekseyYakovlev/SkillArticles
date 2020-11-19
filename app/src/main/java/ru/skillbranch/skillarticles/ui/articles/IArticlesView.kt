package ru.skillbranch.skillarticles.ui.articles

import ru.skillbranch.skillarticles.data.local.entities.ArticleItem

interface IArticlesView {
    fun clickArticle(item: ArticleItem, isBookmarked: Boolean)
}