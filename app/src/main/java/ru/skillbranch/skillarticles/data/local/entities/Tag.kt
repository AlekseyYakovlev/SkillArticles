package ru.skillbranch.skillarticles.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "article_tags")
data class Tag(
    @PrimaryKey
    val tag: String,
    @ColumnInfo(name = "use_count")
    val useCount: Int = 0
)

@Entity(
    tableName = "article_tag_x_ref",
    primaryKeys = ["t_id", "a_id"],
    foreignKeys = [
        ForeignKey(
            entity = Article::class,
            parentColumns = ["id"],
            childColumns = ["a_id"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class ArticleTagXRef(
    @ColumnInfo(name = "a_id")
    val articleId: String,
    @ColumnInfo(name = "t_id")
    val tagId: String
)