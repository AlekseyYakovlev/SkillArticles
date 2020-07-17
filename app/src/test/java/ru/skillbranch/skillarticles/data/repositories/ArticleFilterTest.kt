package ru.skillbranch.skillarticles.data.repositories

import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleFilterTest {
    @Test
    fun test_query_builder() {
        val ALL_ARTICLES = "SELECT * FROM ArticleItem ORDER BY date DESC"
        val BOOKMARKED = "SELECT * FROM ArticleItem WHERE is_bookmark = 1 ORDER BY date DESC"
        val IN_CATEGORIES =
            "SELECT * FROM ArticleItem WHERE category_id IN (0,1) ORDER BY date DESC"
        val SEARCH = "SELECT * FROM ArticleItem WHERE title LIKE '%queryStr%' ORDER BY date DESC"
        val ON_HASHTAG =
            "SELECT * FROM ArticleItem INNER JOIN article_tag_x_ref AS refs ON refs.a_id = id WHERE refs.t_id = '#Android' ORDER BY date DESC"
        val BOOKMARKED_IN_CATEGORIES =
            "SELECT * FROM ArticleItem WHERE is_bookmark = 1 AND category_id IN (0,1) ORDER BY date DESC"
        val BOOKMARKED_SEARCH =
            "SELECT * FROM ArticleItem WHERE title LIKE '%queryStr%' AND is_bookmark = 1 ORDER BY date DESC"
        val BOOKMARKED_ON_HASHTAG =
            "SELECT * FROM ArticleItem INNER JOIN article_tag_x_ref AS refs ON refs.a_id = id WHERE refs.t_id = '#Android' AND is_bookmark = 1 ORDER BY date DESC"
        val SEARCH_IN_CATEGORIES =
            "SELECT * FROM ArticleItem WHERE title LIKE '%queryStr%' AND category_id IN (0,1) ORDER BY date DESC"
        val BOOKMARKED_SEARCH_IN_CATEGORIES =
            "SELECT * FROM ArticleItem WHERE title LIKE '%queryStr%' AND is_bookmark = 1 AND category_id IN (0,1) ORDER BY date DESC"
        val ON_HASHTAG_IN_CATEGORIES =
            "SELECT * FROM ArticleItem INNER JOIN article_tag_x_ref AS refs ON refs.a_id = id WHERE refs.t_id = '#Android' AND category_id IN (0,1) ORDER BY date DESC"
        val BOOKMARKED_ON_HASHTAG_IN_CATEGORIES =
            "SELECT * FROM ArticleItem INNER JOIN article_tag_x_ref AS refs ON refs.a_id = id WHERE refs.t_id = '#Android' AND is_bookmark = 1 AND category_id IN (0,1) ORDER BY date DESC"

        assertEquals(ALL_ARTICLES, ArticleFilter().toQuery())
        assertEquals(BOOKMARKED, ArticleFilter(isBookmark = true).toQuery())
        assertEquals(IN_CATEGORIES, ArticleFilter(categories = listOf("0", "1")).toQuery())
        assertEquals(SEARCH, ArticleFilter(search = "queryStr").toQuery())
        assertEquals(ON_HASHTAG, ArticleFilter(isHashtag = true, search = "#Android").toQuery())
        assertEquals(
            BOOKMARKED_IN_CATEGORIES,
            ArticleFilter(isBookmark = true, categories = listOf("0", "1")).toQuery()
        )
        assertEquals(
            BOOKMARKED_SEARCH,
            ArticleFilter(isBookmark = true, search = "queryStr").toQuery()
        )
        assertEquals(
            BOOKMARKED_ON_HASHTAG,
            ArticleFilter(isBookmark = true, isHashtag = true, search = "#Android").toQuery()
        )
        assertEquals(
            SEARCH_IN_CATEGORIES,
            ArticleFilter(search = "queryStr", categories = listOf("0", "1")).toQuery()
        )
        assertEquals(
            BOOKMARKED_SEARCH_IN_CATEGORIES,
            ArticleFilter(
                isBookmark = true,
                search = "queryStr",
                categories = listOf("0", "1")
            ).toQuery()
        )
        assertEquals(
            ON_HASHTAG_IN_CATEGORIES,
            ArticleFilter(
                isHashtag = true,
                search = "#Android",
                categories = listOf("0", "1")
            ).toQuery()
        )
        assertEquals(
            BOOKMARKED_ON_HASHTAG_IN_CATEGORIES,
            ArticleFilter(
                isBookmark = true,
                isHashtag = true,
                search = "#Android",
                categories = listOf("0", "1")
            ).toQuery()
        )
    }
}
