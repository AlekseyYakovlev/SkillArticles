package ru.skillbranch.skillarticles.viewmodels.articles

import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.ArticleItemData
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState

class ArticlesViewModel(handle: SavedStateHandle) : BaseViewModel<ArticlesState>(handle, ArticlesState()) {
    val repository = ArticlesRepository

    init {
        subscribeOnDataSource(repository.loadArticles()){ articles, state ->
            articles ?: return@subscribeOnDataSource null
            state.copy(articles = articles)
        }
    }
}

data class ArticlesState(val articles: List<ArticleItemData> = emptyList()): IViewModelState
