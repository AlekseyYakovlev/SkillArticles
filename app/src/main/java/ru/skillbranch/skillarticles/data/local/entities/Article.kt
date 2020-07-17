package ru.skillbranch.skillarticles.data.local.entities

import androidx.room.*
import ru.skillbranch.skillarticles.data.local.MarkdownConverter
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import java.util.*

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey()
    val id: String,
    val title: String,
    val description: String,
    @Embedded(prefix = "author_")
    val author: Author,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    val poster: String,
    val date: Date,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)

data class Author(
    @ColumnInfo(name = "user_id")
    val userId: String,
    val avatar: String? = null,
    val name: String
)


@DatabaseView(
    """
        SELECT id, date, author_name AS author, author_avatar, article.title AS title, description, poster, article.category_id AS category_id,
        counts.likes AS like_count, counts.comments AS comment_count, counts.read_duration AS read_duration,
        category.title AS category, category.icon AS category_icon,
        personal.is_bookmark AS is_bookmark
        FROM articles AS article
        INNER JOIN article_counts AS counts ON counts.article_id = id
        INNER JOIN article_categories AS category ON category.category_id = article.category_id
        LEFT JOIN article_personal_infos AS personal ON personal.article_id = id
    """
)
data class ArticleItem(
    val id: String,
    val date: Date = Date(),
    val author: String,
    @ColumnInfo(name = "author_avatar")
    val authorAvatar: String?,
    val title: String,
    val description: String,
    val poster: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    val category: String,
    @ColumnInfo(name = "category_icon")
    val categoryIcon: String,
    @ColumnInfo(name = "like_count")
    val likeCount: Int = 0,
    @ColumnInfo(name = "comment_count")
    val commentCount: Int = 0,
    @ColumnInfo(name = "read_duration")
    val readDuration: Int = 0,
    @ColumnInfo(name = "is_bookmark")
    val isBookmark: Boolean = false
)

@DatabaseView(
    """
        SELECT id, article.title AS title, description, author_user_id, author_avatar, author_name, date, 
        category.category_id AS category_category_id, category.title AS category_title, category.icon AS category_icon,
        content.share_link AS share_link, content.content AS content,
        personal.is_bookmark AS is_bookmark, personal.is_like AS is_like
        FROM articles AS article
        INNER JOIN article_categories AS category ON category.category_id = article.category_id
        LEFT JOIN article_contents AS content ON content.article_id = id
        LEFT JOIN article_personal_infos AS personal ON personal.article_id = id
    """
)
@TypeConverters(MarkdownConverter::class)
data class ArticleFull(
    val id: String,
    val title: String,
    val description: String,
    @Embedded(prefix = "author_")
    val author: Author,
    @Embedded(prefix = "category_")
    val category: Category,
    @ColumnInfo(name = "share_link")
    val shareLink: String? = null,
    @ColumnInfo(name = "is_bookmark")
    val isBookmark: Boolean = false,
    @ColumnInfo(name = "is_like")
    val isLike: Boolean = false,
    val date: Date,
    val content: List<MarkdownElement>? = null
//    val source: String? = null, //TODO implement me
//    val tags: List<String>
)
