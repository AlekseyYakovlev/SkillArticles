package ru.skillbranch.skillarticles.data.repositories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.data.local.DbManager.db
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.local.dao.ArticleContentsDao
import ru.skillbranch.skillarticles.data.local.dao.ArticleCountsDao
import ru.skillbranch.skillarticles.data.local.dao.ArticlePersonalInfosDao
import ru.skillbranch.skillarticles.data.local.dao.ArticlesDao
import ru.skillbranch.skillarticles.data.local.entities.ArticleFull
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.models.CommentItemData
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.extensions.data.toArticleContent
import java.lang.Thread.sleep
import kotlin.math.abs


interface IArticleRepository {
    /**
     * получение настроек приложения из SharedPreferences
     */
    fun getAppSettings(): LiveData<AppSettings>

    /**
     * обновление настроек приложения
     *
     * @param appSettings новые настройки
     */
    fun updateSettings(appSettings: AppSettings)

    /**
     * получение информации о состоянии авторизации пользователя
     */
    fun isAuth(): LiveData<Boolean>

    /**
     * получение полной информации по статье по идентификатору статьи
     *
     * @param articleId идентификатор статьи
     */
    fun findArticle(articleId: String): LiveData<ArticleFull>

    /**
     * получение ArticleContent из сети и сохранение в БД
     *
     * @param articleId идентификатор статьи
     */
    fun fetchArticleContent(articleId: String)

    /**
     * получение количества комментариев к статье по ее идентификатору
     *
     * @param articleId идентификатор статьи
     */
    fun findArticleCommentCount(articleId: String): LiveData<Int>

    /**
     * отправка сообщения и увеличение счетчика сообщений статьи
     *
     * @param articleId идентификатор статьи
     * @param comment текст комментария
     * @param answerToSlug идентификатор статьи
     */
    fun sendMessage(articleId: String, comment: String, answerToSlug: String?)

    /**
     *
     */
    fun loadAllComments(articleId: String, total: Int): CommentsDataFactory

    /**
     *
     */
    fun loadCommentsByRange(slug: String?, size: Int, articleId: String): List<CommentItemData>

    /**
     * инвертирование свойства isLike сущности ArticlePersonalInfo
     */
    fun toggleLike(articleId: String)

    /**
     * инвертирование свойства isBookmark сущности ArticlePersonalInfo
     */
    fun toggleBookmark(articleId: String)

    /**
     * уменьшение свойства likes сущности ArticleCounts на один
     */
    fun decrementLike(articleId: String)

    /**
     * увеличение свойства likes сущности ArticleCounts на один
     */
    fun incrementLike(articleId: String)
}

object ArticleRepository : IArticleRepository {
    private val network = NetworkDataHolder
    private val preferences = PrefManager
    private var articleDao = db.articlesDao()
    private var articlePersonalDao = db.articlePersonalInfosDao()
    private var articleCountsDao = db.articleCountsDao()
    private var articleContentDao = db.articleContentsDao()

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setupTestDao(
        articlesDao: ArticlesDao,
        articlePersonalDao: ArticlePersonalInfosDao,
        articleCountsDao: ArticleCountsDao,
        articleContentDao: ArticleContentsDao
    ) {
        this.articleDao = articlesDao
        this.articlePersonalDao = articlePersonalDao
        this.articleCountsDao = articleCountsDao
        this.articleContentDao = articleContentDao
    }

    override fun findArticle(articleId: String): LiveData<ArticleFull> {
        return articleDao.findFullArticles(articleId)
    }

    override fun getAppSettings(): LiveData<AppSettings> =
        preferences.getAppSettings() //from preferences

    override fun toggleLike(articleId: String) {
        articlePersonalDao.toggleLikeOrInsert(articleId)
    }

    override fun toggleBookmark(articleId: String) {
        articlePersonalDao.toggleBookmarkOrInsert(articleId)
    }

    override fun updateSettings(appSettings: AppSettings) {
        preferences.setAppSettings(appSettings)
    }

    override fun fetchArticleContent(articleId: String) {
        val content = network.loadArticleContent(articleId).apply { sleep(1500) }
        articleContentDao.insert(content.toArticleContent())
    }

    override fun findArticleCommentCount(articleId: String): LiveData<Int> =
        articleCountsDao.getCommentsCount(articleId)


    override fun isAuth(): LiveData<Boolean> = preferences.isAuth()

    override fun loadAllComments(articleId: String, totalCount: Int) =
        CommentsDataFactory(
            itemProvider = ::loadCommentsByRange,
            articleId = articleId,
            totalCount = totalCount
        )

    override fun loadCommentsByRange(
        slug: String?,
        size: Int,
        articleId: String
    ): List<CommentItemData> {
        val data = network.commentsData.getOrElse(articleId) { mutableListOf() }
        return when {
            slug == null -> data.take(size)

            size > 0 -> data.dropWhile { it.slug != slug }
                .drop(1)
                .take(size)

            size < 0 -> data
                .dropLastWhile { it.slug != slug }
                .dropLast(1)
                .takeLast(abs(size))

            else -> emptyList()
        }.apply { sleep(1500) }
    }

    override fun decrementLike(articleId: String) {
        articleCountsDao.decrementLike(articleId)
    }

    override fun incrementLike(articleId: String) {
        articleCountsDao.incrementLike(articleId)
    }

    override fun sendMessage(articleId: String, comment: String, answerToSlug: String?) {
        network.sendMessage(
            articleId, comment, answerToSlug,
            User("777", "John Doe", "https://skill-branch.ru/img/mail/bot/android-category.png")
        )
        articleCountsDao.incrementCommentsCount(articleId)
    }
}

class CommentsDataFactory(
    private val itemProvider: (String?, Int, String) -> List<CommentItemData>,
    private val articleId: String,
    private val totalCount: Int
) : DataSource.Factory<String?, CommentItemData>() {
    override fun create(): DataSource<String?, CommentItemData> =
        CommentsDataSource(itemProvider, articleId, totalCount)

}

class CommentsDataSource(
    private val itemProvider: (String?, Int, String) -> List<CommentItemData>,
    private val articleId: String,
    private val totalCount: Int
) : ItemKeyedDataSource<String, CommentItemData>() {

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<CommentItemData>
    ) {
        val result = itemProvider(params.requestedInitialKey, params.requestedLoadSize, articleId)

        callback.onResult(
            if (totalCount > 0) result else emptyList(),
            0,
            totalCount
        )
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<CommentItemData>) {
        val result = itemProvider(params.key, params.requestedLoadSize, articleId)
        callback.onResult(result)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<CommentItemData>) {
        val result = itemProvider(params.key, -params.requestedLoadSize, articleId)
        callback.onResult(result)
    }

    override fun getKey(item: CommentItemData): String = item.slug
}