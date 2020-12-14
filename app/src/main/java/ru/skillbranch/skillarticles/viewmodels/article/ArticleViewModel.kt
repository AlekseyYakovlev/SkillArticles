package ru.skillbranch.skillarticles.viewmodels.article

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.launch
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.remote.err.ApiError
import ru.skillbranch.skillarticles.data.remote.res.CommentRes
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.data.repositories.CommentsDataFactory
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.data.repositories.clearContent
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.extensions.shortFormat
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import java.util.concurrent.Executors

class ArticleViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
    private val repository: ArticleRepository,
) : BaseViewModel<ArticleState>(handle, ArticleState()), IArticleViewModel {

    private val articleId: String = handle["article_id"]!! //bundle key (safe args from navigation)

    private var clearContent: String? = null
    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
    }

    private val listData: LiveData<PagedList<CommentRes>> =
        Transformations.switchMap(repository.findArticleCommentCount(articleId)) {
            buildPagedList(repository.loadAllComments(articleId, it, ::commentLoadErrorHandler))
        }

    init {
        //subscribe on mutable data
        subscribeOnDataSource(repository.findArticle(articleId)) { article, state ->
            if (article.content == null) fetchContent()
            article.run {
                state.copy(
                    shareLink = shareLink,
                    title = title,
                    category = category.title,
                    categoryIcon = category.icon,
                    date = date.shortFormat(),
                    author = author,
                    source = article.source,
                    tags = article.tags,
                    isBookmark = isBookmark,
                    isLike = isLike,
                    content = content ?: emptyList(),
                    isLoadingContent = content == null
                )
            }
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }

        subscribeOnDataSource(repository.isAuth()) { auth, state ->
            state.copy(isAuth = auth)
        }
    }

    fun refresh() {
        launchSafely {
            launch { repository.fetchArticleContent(articleId) }
            launch { repository.refreshCommentsCount(articleId) }
        }
    }

    private fun commentLoadErrorHandler(throwable: Throwable) {
        throwable.localizedMessage?.let {
            notify(Notify.ErrorMessage(it))
        }
    }

    private val handleLikeErrorHandler: (Throwable) -> Unit = { e ->
        when (e) {
            is ApiError.BadRequest -> Notify.ErrorMessage(e.message)
            else -> { // do nothing
            }
        }
    }

    private fun fetchContent() {
        launchSafely {
            repository.fetchArticleContent(articleId)
        }
    }

    //app settings
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

    //personal article info
    override fun handleBookmark(context: Context) {
        val isBookmark = currentState.isBookmark
        val msg =
            if (!isBookmark) Notify.TextMessage(context.resources.getString(R.string.article_view_model__added_to_bookmarks))
            else {
                Notify.TextMessage(context.resources.getString(R.string.article_view_model__removed_from_bookmarks))
            }

        launchSafely(handleLikeErrorHandler, { notify(msg) }) {
            repository.toggleBookmark(articleId)
        }
    }

    override fun handleLike(context: Context) {
        val isLike = currentState.isLike
        val msg =
            if (!isLike) Notify.TextMessage(context.resources.getString(R.string.article_view_model__marked_as_liked))
            else {
                Notify.ActionMessage(
                    context.resources.getString(R.string.article_view_model__dont_like_it),
                    context.resources.getString(R.string.article_view_model__i_like_it),
                ) { handleLike(context) }
            }
        launchSafely(handleLikeErrorHandler, { notify(msg) }) {
            repository.toggleLike(articleId)
        }
    }

    //not implemented
    override fun handleShare(handleShareCallback: () -> Unit) {
        handleShareCallback()
    }

    //session state
    override fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }

    override fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch, isShowMenu = false, searchPosition = 0) }
    }

    override fun handleSearch(query: String?) {
        query ?: return
        if (clearContent == null && currentState.content.isNotEmpty()) clearContent =
            currentState.content.clearContent()
        val result = clearContent.indexesOf(query)
            .map { it to it + query.length }
        updateState { it.copy(searchQuery = query, searchResults = result, searchPosition = 0) }
    }

    override fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    override fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }

    override fun handleCopyCode(context: Context) {
        notify(Notify.TextMessage(context.resources.getString(R.string.article_view_model__copied_to_clipboard)))
    }

    override fun handleSendComment(context: Context, comment: String?) {
        if (comment.isNullOrBlank()) {
            notify(Notify.TextMessage(context.resources.getString(R.string.article_view_model__comment_must_not_be_empty)))
            return
        }
        updateState { it.copy(commentText = comment) }
        if (!currentState.isAuth) {
            navigate(NavigationCommand.StartLogin())
        } else {
            launchSafely(null, {
                updateState {
                    it.copy(
                        answerTo = null,
                        answerToMessageId = null,
                        commentText = null
                    )
                }
            }) {
                repository.sendMessage(
                    articleId,
                    currentState.commentText!!,
                    currentState.answerToMessageId
                )
            }
        }
    }

    fun observeList(
        owner: LifecycleOwner,
        onChanged: (list: PagedList<CommentRes>) -> Unit
    ) {
        listData.observe(owner, Observer { onChanged(it) })
    }

    private fun buildPagedList(
        dataFactory: CommentsDataFactory
    ): LiveData<PagedList<CommentRes>> {
        return LivePagedListBuilder<String, CommentRes>(
            dataFactory,
            listConfig
        )
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    fun handleCommentFocus(hasFocus: Boolean) {
        updateState { it.copy(showBottomBar = !hasFocus) }
    }

    fun handleClearComment() {
        updateState { it.copy(answerTo = null, answerToMessageId = null, commentText = null) }
    }

    fun handleReplyTo(context: Context, messageId: String, name: String) {
        updateState {
            it.copy(
                answerToMessageId = messageId,
                answerTo = context.resources.getString(R.string.article_view_model__reply_to, name)
            )
        }
    }

}

data class ArticleState(
    val isAuth: Boolean = false, //пользователь авторизован
    val isLoadingContent: Boolean = true, //контент загружается
    val isLoadingReviews: Boolean = true, //отзывы загружаются
    val isLike: Boolean = false, //отмечено как Like
    val isBookmark: Boolean = false, //в закладках
    val isShowMenu: Boolean = false, //отображается меню
    val isBigText: Boolean = false, //шрифт увеличен
    val isDarkMode: Boolean = false, //темный режим
    val isSearch: Boolean = false, //режим поиска
    val searchQuery: String? = null, // поисковы запрос
    val searchResults: List<Pair<Int, Int>> = emptyList(), //результаты поиска (стартовая и конечная позиции)
    val searchPosition: Int = 0, //текущая позиция найденного результата
    val shareLink: String? = null, //ссылка Share
    val title: String? = null, //заголовок статьи
    val category: String? = null, //категория
    val categoryIcon: Any? = null, //иконка категории
    val date: String? = null, //дата публикации
    val author: Any? = null, //автор статьи
    val poster: String? = null, //обложка статьи
    val content: List<MarkdownElement> = emptyList(), //контент
    val commentsCount: Int = 0,
    val answerTo: String? = null,
    val answerToMessageId: String? = null,
    val showBottomBar: Boolean = true,
    val commentText: String? = null,
    val source: String? = null,
    val tags: List<String> = emptyList()

) : IViewModelState {
    override fun save(outState: SavedStateHandle) {

        outState.set("isSearch", isSearch)
        outState.set("searchQuery", searchQuery)
        outState.set("searchResults", searchResults)
        outState.set("searchPosition", searchPosition)
        outState.set("commentText", commentText)
        outState.set("answerTo", answerTo)
        outState.set("answerToSlug", answerToMessageId)
    }

    override fun restore(savedState: SavedStateHandle): ArticleState {
        return copy(
            isSearch = savedState["isSearch"] ?: false,
            searchQuery = savedState["searchQuery"],
            searchResults = savedState["searchResults"] ?: emptyList(),
            searchPosition = savedState["searchPosition"] ?: 0,
            commentText = savedState["commentText"],
            answerTo = savedState["answerTo"],
            answerToMessageId = savedState["answerToSlug"]
        )
    }
}