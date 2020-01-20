package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format

class ArticleViewModel(private val articleId: String) : IArticleViewModel,
    BaseViewModel<ArticleState>(ArticleState()) {
    private val repository = ArticleRepository


    init {
        subscribeOnDataSource(getArticleData()) { article, state ->
            article?.let {
                state.copy(
                    shareLink = it.shareLink,
                    title = it.title,
                    category = it.category,
                    date = it.date.format(),
                    author = it.author,
                    categoryIcon = it.categoryIcon,
                    poster = it.poster
                )
            }
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content?.let {
                state.copy(
                    isLoadingContent = false,
                    content = content

                )
            }
        }

        subscribeOnDataSource(getArticlePersonalInfo()) { info, state ->
            info?.let {
                state.copy(
                    isBookmark = it.isBookmark,
                    isLike = it.isLike
                )
            }
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            settings.let {
                state.copy(
                    isDarkMode = it.isDarkMode,
                    isBigText = it.isBigText
                )
            }
        }

    }

    override fun getArticleContent(): LiveData<List<Any>?> {
        return repository.loadArticleContent(articleId)
    }

    override fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    override fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    override fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    override fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    override fun handleBookmark() {
        repository.updateArticlePersonalInfo(currentState.toArticlePersonalInfo().copy(isBookmark = !currentState.isBookmark))

        val msg =
            if (currentState.isBookmark) Notify.TextMessage("Add to bookmarks")
            else Notify.TextMessage("Remove from bookmarks")

        notify(msg)

    }

    override fun handleLike() {

        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()

        val msg =
            if (currentState.isLike) Notify.TextMessage("Mark is liked")
            else { //FIXME change to "Marked as liked"
                Notify.ActionMessage(
                    "Don`t like it anymore", //FIXME change to "Don't like it anymore?"
                    "No, still like it",
                    toggleLike
                )
            }
        notify(msg)
    }

    override fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }

    override fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }

    override fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch) }
    }

    override fun handleSearch(query: String?) {
        updateState { it.copy(searchQuery = query) }
    }


}

data class ArticleState(
    val isAuth: Boolean = false,
    val isLoadingContent: Boolean = true,
    val isLoadingReviews: Boolean = true,
    val isLike: Boolean = false,
    val isBookmark: Boolean = false,
    val isShowMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false,
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val searchResults: List<Pair<Int, Int>> = emptyList(),
    val searchPosition: Int = 0,
    val shareLink: String? = null,
    val title: String? = null,
    val category: String? = null,
    val categoryIcon: Any? = null,
    val date: String? = null,
    val author: Any? = null,
    val poster: String? = null,
    val content: List<Any> = emptyList(),
    val reviews: List<Any> = emptyList()

)