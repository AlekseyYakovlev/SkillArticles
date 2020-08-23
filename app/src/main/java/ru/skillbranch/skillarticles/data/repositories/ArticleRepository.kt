package ru.skillbranch.skillarticles.data.repositories

import android.util.Log
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
import ru.skillbranch.skillarticles.data.remote.err.ApiError
import ru.skillbranch.skillarticles.data.remote.err.NoNetworkError
import ru.skillbranch.skillarticles.data.remote.req.MessageReq
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
     * @param message текст комментария
     * @param answerToMessageId идентификатор статьи
     */
    suspend fun sendMessage(articleId: String, message: String, answerToMessageId: String?)

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
    suspend fun toggleLike(articleId: String): Boolean

    /**
     * инвертирование свойства isBookmark сущности ArticlePersonalInfo
     */
    suspend fun toggleBookmark(articleId: String): Boolean

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
        preferences.appSettings //from preferences

    override suspend fun toggleLike(articleId: String): Boolean {
        return if (articlePersonalDao.toggleLikeOrInsert(articleId)) {
            //incrementLike(articleId)
            true
        } else {
            //decrementLike(articleId)
            false
        }

//        return if (articlePersonalDao.isLiked(articleId)) {
//
//            decrementLike(articleId)
//            Log.d("123456"," dec like")
//            false
//        } else {
//
//            Log.d("123456"," inc like")
//            true
//        }
    }


    override suspend fun toggleBookmark(articleId: String): Boolean =
        if (articlePersonalDao.isBookmarked(articleId)) {
            removeBookmark(articleId)
            false
        } else {
            addBookmark(articleId)
            true
        }

    // articlePersonalDao.toggleBookmarkOrInsert(articleId)


    override fun updateSettings(appSettings: AppSettings) {
        preferences.setAppSettings(appSettings)
    }

    override fun isAuth(): LiveData<Boolean> = preferences.isAuthLive


    override suspend fun decrementLike(articleId: String) {
       // articlePersonalDao.setLikeOrInsert(articleId, false)

        if (preferences.accessToken.isEmpty()) {
            articleCountsDao.decrementLike(articleId)
            return
        }

        try {
            val res = network.decrementLike(articleId, preferences.accessToken)
            articleCountsDao.updateLike(articleId, res.likeCount)
        } catch (e: Exception) {
            when (e) {
                is NoNetworkError -> articleCountsDao.decrementLike(articleId)
                is ApiError -> throw e
            }
        }
    }

    override suspend fun incrementLike(articleId: String) {
       // articlePersonalDao.setLikeOrInsert(articleId, true)

        if (preferences.accessToken.isEmpty()) {
            articleCountsDao.incrementLike(articleId)
            return
        }

        try {
            val res = network.incrementLike(articleId, preferences.accessToken)
            articleCountsDao.updateLike(articleId, res.likeCount)
        } catch (e: Exception) {
            when (e) {
                is NoNetworkError -> articleCountsDao.incrementLike(articleId)
                is ApiError -> throw e
            }
        }
    }

    override suspend fun sendMessage(
        articleId: String,
        message: String,
        answerToMessageId: String?
    ) {
        val (_, messageCount) = network.sendMessage(
            articleId,
            MessageReq(message, answerToMessageId),
            preferences.accessToken
        )
        Log.d("123456", messageCount.toString())
        articleCountsDao.updateCommentsCount(articleId, messageCount)
    }

    suspend fun refreshCommentsCount(articleId: String) {
        val counts = network.loadArticleCounts(articleId)
        articleCountsDao.updateCommentsCount(articleId, counts.comments)
    }

    override suspend fun fetchArticleContent(articleId: String) {
        val content = network.loadArticleContent(articleId)
        articleContentDao.insert(content.toArticleContent())
    }

    override fun findArticleCommentCount(articleId: String): LiveData<Int> =
        articleCountsDao.getCommentsCount(articleId)

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

    suspend fun addBookmark(articleId: String) {
        articlePersonalDao.setBookmarkOrInsert(articleId, true)
        if (preferences.accessToken.isEmpty()) {
            return
        }

        try {
            network.addBookmark(articleId, preferences.accessToken)
        } catch (e: Exception) {
            when (e) {
                is NoNetworkError -> articleCountsDao.incrementLike(articleId)
                is ApiError -> throw e
            }
        }
    }

    suspend fun removeBookmark(articleId: String) {
        articlePersonalDao.setBookmarkOrInsert(articleId, false)
        if (preferences.accessToken.isEmpty()) {
            return
        }

        try {
            network.removeBookmark(articleId, preferences.accessToken)
        } catch (e: Exception) {
            when (e) {
                is NoNetworkError -> articleCountsDao.incrementLike(articleId)
                is ApiError -> throw e
            }
        }
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