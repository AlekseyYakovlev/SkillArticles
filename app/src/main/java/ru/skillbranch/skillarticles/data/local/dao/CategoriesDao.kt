package ru.skillbranch.skillarticles.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.skillbranch.skillarticles.data.local.entities.Category
import ru.skillbranch.skillarticles.data.local.entities.CategoryData

@Dao
interface CategoriesDao : BaseDao<Category> {
    @Query(
        """
            SELECT c.title AS title, c.icon, c.category_id AS category_id, 
            COUNT(a.category_id) AS articles_count 
            FROM article_categories AS c 
            INNER JOIN articles AS a ON c.category_id = a.category_id
            GROUP BY c.category_id
            ORDER BY articles_count DESC
        """
    )
    fun findAllCategoriesData(): LiveData<List<CategoryData>>
}