package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ru.skillbranch.skillarticles.data.*

object ArticleRepository {
    private val local = LocalDataHolder
    private val network = NetworkDataHolder

    fun loadArticleContent(articleId: String): LiveData<List<MarkdownElement>?> {
        return Transformations.map(network.loadArticleContent(articleId)){
            return@map  if(it == null) null
            else MarkdownParser.parse(it)
        }
    }
    fun getArticle(articleId: String): LiveData<ArticleData?> {
        return local.findArticle(articleId) //2s delay from db
    }

    fun loadArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        return local.findArticlePersonalInfo(articleId) //1s delay from db
    }

    fun getAppSettings(): LiveData<AppSettings> = local.getAppSettings() //from preferences
    fun updateSettings(appSettings: AppSettings) {
        local.updateAppSettings(appSettings)
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        local.updateArticlePersonalInfo(info)
    }

    fun isAuth(): MutableLiveData<Boolean> = local.isAuth()
}