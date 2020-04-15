package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleItemData
import ru.skillbranch.skillarticles.data.LocalDataHolder

object  ArticlesRepository {
    fun loadArticles(): LiveData<List<ArticleItemData>?> = LocalDataHolder.findArticles()

}