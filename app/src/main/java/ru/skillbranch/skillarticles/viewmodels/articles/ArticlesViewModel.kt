package ru.skillbranch.skillarticles.viewmodels.articles

import androidx.lifecycle.SavedStateHandle
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState

class ArticlesViewModel(handle: SavedStateHandle) :
    BaseViewModel<ArticlesState>(handle, ArticlesState()) {
    private val repository = ArticlesRepository
    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .setPrefetchDistance(30)
            .setInitialLoadSizeHint(50)
            .build()
    }

    init {
        subscribeOnDataSource(repository.loadArticles()) { articles, state ->
            articles ?: return@subscribeOnDataSource null
            state.copy(articles = articles)
        }
    }

    private fun buildPagedList(
        dataFactory: DataSource.Factory<Int, ArticleItemData>
    ) {
        val builder = LivePagedListBuilder<Int, ArticleItemData>(
            dataFactory,
            listConfig
        )
    }
}

data class ArticlesState(val articles: List<ArticleItemData> = emptyList()) : IViewModelState
