package ru.skillbranch.skillarticles.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.skillarticles.data.EntityGenerator.generateArticleItems
import ru.skillbranch.skillarticles.data.EntityGenerator.generateComments
import ru.skillbranch.skillarticles.data.models.*
import java.util.*

object LocalDataHolder {
    private val articleInfo = MutableLiveData<ArticlePersonalInfo?>(null)
    private val settings = MutableLiveData(AppSettings())
    private val isAuth = MutableLiveData(false)
    val localArticleItems: MutableList<ArticleItemData> = mutableListOf()
    val localArticles: MutableMap<String, MutableLiveData<ArticleData>> = mutableMapOf()

    fun findArticle(articleId: String): LiveData<ArticleData?> {
        if (localArticles[articleId] == null) {
            val article = localArticleItems.find { it.id == articleId }
            localArticles[articleId] = MutableLiveData(EntityGenerator.generateArticle(article ?: EntityGenerator.createArticleItem(articleId)))
        }
        return localArticles[articleId]!!
    }

    fun findArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        GlobalScope.launch {
            delay(500)
            articleInfo.postValue(ArticlePersonalInfo(isBookmark = true))
        }
        return articleInfo
    }

    fun getAppSettings() = settings
    fun updateAppSettings(appSettings: AppSettings) {
        settings.value = appSettings
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        articleInfo.value = info
    }

    fun isAuth(): MutableLiveData<Boolean> = isAuth

    fun setAuth(auth: Boolean) {
        isAuth.value = auth
    }

    fun incrementCommentsCount(articleId: String) {
        val old =
            localArticles[articleId]?.value ?: error("Local article with id: $articleId not found")
        localArticles[articleId]!!.postValue(old.copy(commentCount = old.commentCount.inc()))
    }
}

object NetworkDataHolder {

    val networkArticleItems: List<ArticleItemData> = generateArticleItems(200)

    val commentsData: Map<String, MutableList<CommentItemData>> by lazy {
        networkArticleItems.associate { article ->
            article.id to generateComments(article.id, article.commentCount) as MutableList
        }
    }

    fun loadArticleContent(articleId: String): LiveData<String?> {
        val content = MutableLiveData<String?>(null)
        GlobalScope.launch {
            delay(1500)
            val s = articlesContent[articleId.toInt() % 6]
            content.postValue(s)
        }
        return content
    }

    fun sendMessage(articleId: String, text: String, answerToSlug: String?, user: User) {
        val mutableList =
            commentsData[articleId] ?: error("Comments for article id : $articleId not found")
        val index =
            if (answerToSlug == null) 0 else mutableList.indexOfFirst { it.slug == answerToSlug }.inc()
        val mess = mutableList.getOrNull(index.dec())
        val id = "${mutableList.size}"
        mutableList.add(
            index,
            CommentItemData(
                id,
                articleId,
                user,
                body = text,
                slug = "${answerToSlug ?: ""}$id/",
                answerTo = mess?.user?.name,
                date = Date()
            )
        )
    }
}





