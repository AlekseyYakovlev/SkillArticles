package ru.skillbranch.skillarticles.data.repositories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import ru.skillbranch.skillarticles.data.local.DbManager.db
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.local.dao.ArticleContentsDao
import ru.skillbranch.skillarticles.data.local.dao.ArticleCountsDao
import ru.skillbranch.skillarticles.data.local.dao.ArticlePersonalInfosDao
import ru.skillbranch.skillarticles.data.local.dao.ArticlesDao
import ru.skillbranch.skillarticles.data.local.entities.ArticleFull
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.res.CommentRes
import ru.skillbranch.skillarticles.extensions.data.toArticleContent


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
    suspend fun fetchArticleContent(articleId: String)

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
    suspend fun sendMessage(articleId: String, comment: String, answerToSlug: String?)

    /**
     *
     */
    fun loadAllComments(
        articleId: String,
        totalCount: Int,
        errHandler: (Throwable) -> Unit
    ): CommentsDataFactory

    /**
     * инвертирование свойства isLike сущности ArticlePersonalInfo
     */
    suspend fun toggleLike(articleId: String)

    /**
     * инвертирование свойства isBookmark сущности ArticlePersonalInfo
     */
    suspend fun toggleBookmark(articleId: String)

    /**
     * уменьшение свойства likes сущности ArticleCounts на один
     */
    suspend fun decrementLike(articleId: String)

    /**
     * увеличение свойства likes сущности ArticleCounts на один
     */
    suspend fun incrementLike(articleId: String)

}

object ArticleRepository : IArticleRepository {
    private val network = NetworkManager.api
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

    override suspend fun toggleLike(articleId: String) {
        articlePersonalDao.toggleLikeOrInsert(articleId)
    }

    override suspend fun toggleBookmark(articleId: String) {
        articlePersonalDao.toggleBookmarkOrInsert(articleId)
    }

    override fun updateSettings(appSettings: AppSettings) {
        preferences.setAppSettings(appSettings)
    }

    override suspend fun fetchArticleContent(articleId: String) {
        val content = network.loadArticleContent(articleId)
        articleContentDao.insert(content.toArticleContent())
    }

    override fun findArticleCommentCount(articleId: String): LiveData<Int> =
        articleCountsDao.getCommentsCount(articleId)


    override fun isAuth(): LiveData<Boolean> = preferences.isAuth()

    override fun loadAllComments(
        articleId: String,
        totalCount: Int,
        errHandler: (Throwable) -> Unit
    ) =
        CommentsDataFactory(
            itemProvider = network,
            articleId = articleId,
            totalCount = totalCount,
            errHandler = errHandler
        )


    override suspend fun decrementLike(articleId: String) {
        articleCountsDao.decrementLike(articleId)
    }

    override suspend fun incrementLike(articleId: String) {
        articleCountsDao.incrementLike(articleId)
    }

    override suspend fun sendMessage(articleId: String, comment: String, answerToSlug: String?) {
//        network.sendMessage(
//            articleId, comment, answerToSlug,
//            User("777", "John Doe", "https://skill-branch.ru/img/mail/bot/android-category.png")
//        )
//        articleCountsDao.incrementCommentsCount(articleId)
    }

    suspend fun refreshCommentsCount(articleId: String) {
        val counts = network.loadArticleCounts(articleId)
        articleCountsDao.updateCommentsCount(articleId, counts.comments)
    }
}

class CommentsDataFactory(
    private val itemProvider: RestService,
    private val articleId: String,
    private val totalCount: Int,
    private val errHandler: (Throwable) -> Unit
) : DataSource.Factory<String?, CommentRes>() {
    override fun create(): DataSource<String?, CommentRes> =
        CommentsDataSource(itemProvider, articleId, totalCount, errHandler)

}

class CommentsDataSource(
    private val itemProvider: RestService,
    private val articleId: String,
    private val totalCount: Int,
    private val errHandler: (Throwable) -> Unit
) : ItemKeyedDataSource<String, CommentRes>() {

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<CommentRes>
    ) {
        try {
            val result = itemProvider.loadComments(
                articleId,
                params.requestedInitialKey,
                params.requestedLoadSize
            ).execute()

            callback.onResult(
                if (totalCount > 0) result.body()!! else emptyList(),
                0,
                totalCount
            )
        } catch (e: Throwable) {
            errHandler(e)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<CommentRes>) {
        try {
            val result = itemProvider.loadComments(
                articleId,
                params.key,
                params.requestedLoadSize
            ).execute()

            callback.onResult(result.body()!!)
        } catch (e: Throwable) {
            errHandler(e)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<CommentRes>) {
        try {
            val result = itemProvider.loadComments(
                articleId,
                params.key,
                -params.requestedLoadSize
            ).execute()

            callback.onResult(result.body()!!)
        } catch (e: Throwable) {
            errHandler(e)
        }
    }

    override fun getKey(item: CommentRes): String = item.id
}