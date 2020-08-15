package ru.skillbranch.skillarticles.data.remote


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.skillarticles.data.remote.res.ArticleContentRes
import ru.skillbranch.skillarticles.data.remote.res.ArticleCountsRes
import ru.skillbranch.skillarticles.data.remote.res.ArticleRes
import ru.skillbranch.skillarticles.data.remote.res.CommentRes

interface RestService {
    //https://skill-articles.skill-branch.ru/api/v1/articles?last=articleId&limit=10
    @GET("articles")
    suspend fun articles(
        @Query("last") last: String? = null,
        @Query("limit") limit: Int = 10
    ): List<ArticleRes>

    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/content
    @GET("articles/{article}/content")
    suspend fun loadArticleContent(
        @Path("article") articleId: String
    ): ArticleContentRes

    //1:39:00
    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/messages
    @GET("articles/{article}/messages")
    fun loadComments(
        @Path("article") articleId: String,
        @Query("last") last: String? = null,
        @Query("limit") limit: Int = 10
    ): Call<List<CommentRes>>

    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/counts
    @GET("articles/{article}/counts")
    suspend fun loadArticleCounts(@Path("article") articleId: String): ArticleCountsRes
}