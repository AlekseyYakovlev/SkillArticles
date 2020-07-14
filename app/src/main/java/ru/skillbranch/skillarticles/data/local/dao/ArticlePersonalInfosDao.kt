package ru.skillbranch.skillarticles.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.skillbranch.skillarticles.data.local.entities.ArticlePersonalInfo

@Dao
interface ArticlePersonalInfosDao : BaseDao<ArticlePersonalInfo> {
    /**
     * Insert or update an array of objects from the database.
     *
     * @param objList the object to be updated
     */
    @Transaction
    fun upsert(objList: List<ArticlePersonalInfo>) {
        insert(objList)
            .mapIndexed { index, l -> if (l == -1L) objList[index] else null }
            .filterNotNull()
            .also {
                if (it.isNotEmpty()) update(it)
            }
    }

    @Query(
        """
            UPDATE article_personal_infos 
            SET is_bookmark = NOT is_bookmark, updated_at = CURRENT_TIMESTAMP
            WHERE article_id = :articleId
        """
    )
    fun toggleLike(articleId: String): Int

    @Query(
        """
            UPDATE article_personal_infos 
            SET is_like = NOT is_like, updated_at = CURRENT_TIMESTAMP
            WHERE article_id = :articleId
        """
    )
    fun toggleBookmark(articleId: String): Int

    @Transaction
    fun toggleLikeOrInsert(articleId: String) {
        if (toggleLike(articleId) == 0) insert(
            ArticlePersonalInfo(
                articleId = articleId,
                isLike = true
            )
        )
    }

    @Transaction
    fun toggleBookmarkOrInsert(articleId: String) {
        if (toggleBookmark(articleId) == 0) insert(
            ArticlePersonalInfo(
                articleId = articleId,
                isBookmark = true
            )
        )
    }
}