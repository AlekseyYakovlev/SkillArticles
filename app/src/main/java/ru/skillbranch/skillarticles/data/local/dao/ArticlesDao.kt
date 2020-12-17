package ru.skillbranch.skillarticles.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.local.entities.ArticleFull
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem

@Dao
interface ArticlesDao : BaseDao<Article> {
    /**
     * Insert or update an array of objects from the database.
     *
     * @param objList the object to be updated
     */
    @Transaction
    suspend fun upsert(objList: List<Article>) {
        insert(objList)
            .mapIndexed { index, l -> if (l == -1L) objList[index] else null }
            .filterNotNull()
            .also {
                if (it.isNotEmpty()) update(it)
            }
    }

    @Query(
        """
            SELECT * FROM articles
        """
    )
    fun findArticles(): LiveData<List<Article>>

    @Query(
        """
            SELECT * 
            FROM articles
            WHERE id = :id            
            LIMIT 1
        """
    )
    fun findArticleById(id: String): LiveData<Article>

    @Query(
        """
            SELECT * FROM ArticleItem
        """
    )
    fun findArticleItems(): LiveData<List<ArticleItem>>

    @Query(
        """
            SELECT * 
            FROM ArticleItem
            WHERE category_id IN (:categoryIds)
        """
    )
    fun findArticleItemsByCategoryIds(categoryIds: List<String>): LiveData<List<ArticleItem>>

    @Query(
        """
            SELECT a.* 
            FROM ArticleItem AS a
            INNER JOIN article_tag_x_ref AS refs ON refs.a_id = a.id 
            WHERE refs.t_id = :tag
        """
    )
    fun findArticlesByTagId(tag: String): LiveData<List<ArticleItem>>

    @RawQuery(observedEntities = [ArticleItem::class])
    fun findArticlesByRaw(simpleSQLiteQuery: SimpleSQLiteQuery): DataSource.Factory<Int, ArticleItem>

    @Query(
        """
            SELECT * 
            FROM ArticleFull
            WHERE id = :articleId            
            LIMIT 1
        """
    )
    fun findFullArticles(articleId: String): LiveData<ArticleFull>

    @Query(
        """
            SELECT id 
            FROM articles
            ORDER BY date DESC           
            LIMIT 1
        """
    )
    fun findLastArticleId(): String?

    @Query("SELECT * FROM articles")
    suspend fun findArticlesTest(): List<Article>


    @Query(
        """
            DELETE FROM articles
            WHERE id = :articleId     
        """
    )
    fun deleteById(articleId: String)
}