package ru.skillbranch.skillarticles.viewmodels.articles

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.data.repositories.ArticleStrategy
import ru.skillbranch.skillarticles.data.repositories.ArticlesDataFactory
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import java.util.concurrent.Executors

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
    private val listData = Transformations.switchMap(state) {
        val searchFn = if (!it.isBookmark) repository::searchArticles
        else repository::searchBookmarkedArticles

        val defaultFn = if (!it.isBookmark) repository::allArticles
        else repository::allBookmarked

        when {
            it.isSearch && !it.searchQuery.isNullOrBlank() -> buildPagedList(
                searchFn(it.searchQuery)
            )
            else -> buildPagedList(defaultFn())
        }
    }

    fun observeList(
        owner: LifecycleOwner,
        isBookmark: Boolean = false,
        onChange: (list: PagedList<ArticleItemData>) -> Unit
    ) {
        updateState { it.copy(isBookmark = isBookmark) }
        listData.observe(owner, Observer { onChange(it) })
    }

    private fun buildPagedList(
        dataFactory: ArticlesDataFactory
    ): LiveData<PagedList<ArticleItemData>> {
        val builder = LivePagedListBuilder<Int, ArticleItemData>(
            dataFactory,
            listConfig
        )

        //if all articles
        if (dataFactory.strategy is ArticleStrategy.AllArticles) {
            builder.setBoundaryCallback(
                ArticlesBoundaryCallback(
                    ::zeroLoadingHandle,
                    ::itemAtEndHandle
                )
            )
        }

        return builder
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    private fun itemAtEndHandle(lastLoadArticle: ArticleItemData) {
        viewModelScope.launch(Dispatchers.IO) {
            val items = repository.loadArticlesFromNetwork(
                start = lastLoadArticle.id.toInt().inc(),
                size = listConfig.pageSize
            )
            if (items.isNotEmpty()) {
                repository.insertArticlesToDb(items)
                //invalidate data in data source -> create new LiveData<PagedList>
                listData.value?.dataSource?.invalidate()
            }

            withContext(Dispatchers.Main) {
                notify(
                    Notify.TextMessage(
                        "Load from network articles from ${items.firstOrNull()?.id} " +
                                "to ${items.lastOrNull()?.id}"
                    )
                )
            }
        }
    }

    private fun zeroLoadingHandle() {
        notify(Notify.TextMessage("Storage is empty"))
        viewModelScope.launch(Dispatchers.IO) {
            val items =
                repository.loadArticlesFromNetwork(
                    start = 0,
                    size = listConfig.initialLoadSizeHint
                )
            if (items.isNotEmpty()) {
                repository.insertArticlesToDb(items)
                //invalidate data in data source -> create new LiveData<PagedList>
                listData.value?.dataSource?.invalidate()
            }
        }
    }

    fun handleSearch(query: String?) {
        query ?: return
        updateState { it.copy(searchQuery = query) }
    }

    fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch) }
    }

    fun handleToggleBookmark(id: String, isChecked: Boolean) {
        repository.updateBookmark(id, !isChecked)
        listData.value?.dataSource?.invalidate()
    }
}

data class ArticlesState(
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true,
    val isBookmark: Boolean = false
) : IViewModelState


class ArticlesBoundaryCallback(
    private val zeroLoadingHandle: () -> Unit,
    private val itemAtEndHandle: (ArticleItemData) -> Unit

) : PagedList.BoundaryCallback<ArticleItemData>() {
    override fun onZeroItemsLoaded() {
        //Storage is empty
        zeroLoadingHandle()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ArticleItemData) {
        //user scroll down -> need load more items
        itemAtEndHandle(itemAtEnd)
    }
}