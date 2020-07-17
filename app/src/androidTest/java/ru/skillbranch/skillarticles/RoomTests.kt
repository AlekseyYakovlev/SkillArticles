package ru.skillbranch.skillarticles

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.DataSource
import androidx.room.Room
import androidx.room.paging.LimitOffsetDataSource
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jraska.livedata.TestObserver
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.skillbranch.skillarticles.data.local.AppDb
import ru.skillbranch.skillarticles.data.local.entities.*
import ru.skillbranch.skillarticles.data.repositories.ArticleFilter
import java.util.*


@RunWith(AndroidJUnit4::class)
class RoomTests {
    @get:Rule
    var testRule = InstantTaskExecutorRule()
    private lateinit var testDb: AppDb

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDb::class.java
        ).build()
    }

    @After
    fun closeDb() {
        testDb.close()
    }


    @Test
    fun test_insert_one() {
        val expectedArticle = Article(
            id = "0",
            title = "test article",
            description = "test description",
            categoryId = "0",
            poster = "anyurl",
            date = Date(),
            updatedAt = Date(),
            author = Author(userId = "0", avatar = "any url", name = "John Doe")
        )

        testDb.articlesDao().insert(expectedArticle)
        val actualArticle = testDb.articlesDao().findArticleById(expectedArticle.id)
        TestObserver.test(actualArticle)
            .assertHasValue()
            .assertValue(expectedArticle)
    }

    @Test
    fun test_insert_many() {
        val expectedArticle = Article(
            id = "0",
            title = "test article",
            description = "test description",
            categoryId = "0",
            poster = "anyurl",
            date = Date(),
            updatedAt = Date(),
            author = Author(userId = "0", avatar = "any url", name = "John Doe")
        )
        val expectedArticles = Array(3) {
            expectedArticle.copy(id = "$it")
        }.toList()

        testDb.articlesDao().insert(expectedArticle.copy(title = "insert first"))

        val actualArticles = testDb.articlesDao().findArticles()
        val testObs = TestObserver.test(actualArticles)

        testObs
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue(listOf(expectedArticle.copy(title = "insert first")))

        testDb.articlesDao().upsert(expectedArticles)

        testObs
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue(expectedArticles)
    }

    @Test
    fun test_insert_many_with_counts() {
        val expectedArticles = Array(3) {
            Article(
                id = "$it",
                title = "test article",
                description = "test description",
                categoryId = "0",
                poster = "anyurl",
                date = Date(),
                updatedAt = Date(),
                author = Author(userId = "0", avatar = "any url", name = "John Doe")
            )
        }.toList()
        val expectedCounts = Array(3) {
            ArticleCounts(
                articleId = "$it",
                likes = (1..200).random(),
                comments = (20..40).random(),
                readDuration = 10
            )
        }.toList()
        val expectedArticleItems = Array(3) {
            ArticleItem(
                id = "$it",
                date = expectedArticles[it].date,
                author = expectedArticles[it].author.name,
                authorAvatar = expectedArticles[it].author.avatar,
                title = expectedArticles[it].title,
                description = expectedArticles[it].description,
                poster = expectedArticles[it].poster,
                likeCount = expectedCounts[it].likes,
                commentCount = expectedCounts[it].comments,
                readDuration = expectedCounts[it].readDuration,
                categoryId = "0",
                category = "Android",
                categoryIcon = "any url"
            )
        }.toList()

        testDb.categoriesDao()
            .insert(Category(categoryId = "0", title = "Android", icon = "any url"))
        testDb.articlesDao().upsert(expectedArticles)
        testDb.articleCountsDao().upsert(expectedCounts)
        val actualArticleItems = testDb.articlesDao().findArticleItems()
        TestObserver.test(actualArticleItems)
            .assertHasValue()
            .assertValue(expectedArticleItems)
    }

    @Test
    fun test_foreign_key_with_counts() {
        val expectedArticles = Array(3) {
            Article(
                id = "$it",
                title = "test article",
                description = "test description",
                categoryId = "0",
                poster = "anyurl",
                date = Date(),
                updatedAt = Date(),
                author = Author(userId = "0", avatar = "any url", name = "John Doe")
            )
        }.toList()
        val expectedCounts = Array(3) {
            ArticleCounts(
                articleId = "$it",
                likes = (1..200).random(),
                comments = (20..40).random(),
                readDuration = 10
            )
        }.toList()

        testDb.articlesDao().upsert(expectedArticles)
        testDb.articleCountsDao().upsert(expectedCounts)
        testDb.categoriesDao()
            .insert(Category(categoryId = "0", title = "Android", icon = "any url"))

        val actualArticleItems = testDb.articlesDao().findArticleItems()
        val actualArticleCounts = testDb.articleCountsDao().findArticleCounts()

        val testItems = TestObserver.test(actualArticleItems)
        val testCounts = TestObserver.test(actualArticleCounts)

        testItems
            .assertHasValue()
            .assertValue { it.size == 3 }

        testCounts
            .assertHasValue()
            .assertValue { it.size == 3 }

        testDb.articlesDao().delete(expectedArticles[1])

        testItems
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue { it.size == 2 }

        testCounts
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue { it.size == 2 }
    }


    @Test
    fun test_update_counts() {
        val expectedArticles = Array(3) {
            Article(
                id = "$it",
                title = "test article",
                description = "test description",
                categoryId = "0",
                poster = "anyurl",
                date = Date(),
                updatedAt = Date(),
                author = Author(userId = "0", avatar = "any url", name = "John Doe")
            )
        }.toList()
        val expectedCounts = Array(3) {
            ArticleCounts(
                articleId = "$it",
                likes = if (it == 0) 0 else (1..200).random(),
                comments = (20..40).random(),
                readDuration = 10
            )
        }.toList()

        testDb.articlesDao().upsert(expectedArticles)
        testDb.articleCountsDao().upsert(expectedCounts)
        testDb.categoriesDao().insert(
            Category(
                categoryId = "0",
                title = "Android",
                icon = "any url"
            )
        )

        val actualArticleItems = testDb.articlesDao().findArticleItems()
        val testItems = TestObserver.test(actualArticleItems)

        testItems
            .assertHasValue()
            .assertValue { it.first().likeCount == 0 }

        testDb.articleCountsDao().decrementLike("0")

        testItems
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue { it.first().likeCount == 0 }

        testDb.articleCountsDao().incrementLike("0")
        testDb.articleCountsDao().incrementLike("1")
        testDb.articleCountsDao().incrementLike("1")
        testDb.articleCountsDao().decrementLike("2")
        testDb.articleCountsDao().incrementCommentsCount("2")

        testItems
            .assertHasValue()
            .assertHistorySize(7)
            .assertValue { it[0].likeCount == 1 }
            .assertValue { it[1].likeCount == expectedCounts[1].likes.plus(2) }
            .assertValue { it[2].likeCount == expectedCounts[2].likes.dec() }
            .assertValue { it[2].commentCount == expectedCounts[2].comments.inc() }
    }

    @Test
    fun test_items_by_category() {
        val expectedArticles = Array(3) {
            Article(
                id = "$it",
                title = "test article",
                description = "test description",
                categoryId = if (it == 1) "1" else "0",
                poster = "anyurl",
                date = Date(),
                updatedAt = Date(),
                author = Author(userId = "0", avatar = "any url", name = "John Doe")
            )
        }.toList()
        val expectedCounts = Array(3) {
            ArticleCounts(
                articleId = "$it",
                likes = if (it == 0) 0 else (1..200).random(),
                comments = (20..40).random(),
                readDuration = 10
            )
        }.toList()

        testDb.categoriesDao()
            .insert(Category(categoryId = "0", title = "Android", icon = "any url"))
        testDb.categoriesDao().insert(Category(categoryId = "1", title = "iOS", icon = "any url"))
        testDb.articlesDao().upsert(expectedArticles)
        testDb.articleCountsDao().upsert(expectedCounts)

        val actualArticleItems = testDb.articlesDao().findArticleItemsByCategoryIds(listOf("0"))
        TestObserver.test(actualArticleItems)
            .assertHasValue()
            .assertValue { it.size == 2 }
            .assertValue { it[0].category == "Android" }
    }


    @Test
    fun test_category_data() {
        val expectedArticles = Array(3) {
            Article(
                id = "$it",
                title = "test article",
                description = "test description",
                categoryId = if (it == 1) "1" else "0",
                poster = "anyurl",
                date = Date(),
                updatedAt = Date(),
                author = Author(userId = "0", avatar = "any url", name = "John Doe")
            )
        }.toList()
        val expectedCounts = Array(3) {
            ArticleCounts(
                articleId = "$it",
                likes = if (it == 0) 0 else (1..200).random(),
                comments = (20..40).random(),
                readDuration = 10
            )
        }.toList()

        testDb.categoriesDao()
            .insert(Category(categoryId = "0", title = "Android", icon = "any url"))
        testDb.categoriesDao().insert(Category(categoryId = "1", title = "iOS", icon = "any url"))
        testDb.articlesDao().upsert(expectedArticles)
        testDb.articleCountsDao().upsert(expectedCounts)
        val actualCategories = testDb.categoriesDao().findAllCategoriesData()
        TestObserver.test(actualCategories)
            .assertHasValue()
            .assertValue(
                listOf(
                    CategoryData(
                        categoryId = "0",
                        icon = "any url",
                        title = "Android",
                        articlesCount = 2
                    ),
                    CategoryData(
                        categoryId = "1",
                        icon = "any url",
                        title = "iOS",
                        articlesCount = 1
                    )
                )
            )
    }

    @Test
    fun test_toggle_or_insert() {

        testDb.articlesDao().insert(
            Article(
                id = "0",
                categoryId = "0",
                title = "test",
                poster = "test_poster",
                description = "test_description",
                author = Author(userId = "0", name = "John Doe", avatar = "any url"),
                date = Date(),
                updatedAt = Date()
            )
        )
        testDb.articleCountsDao().insert(
            ArticleCounts(
                articleId = "0",
                likes = 0,
                comments = (20..40).random(),
                readDuration = 10
            )
        )

        testDb.categoriesDao().insert(
            Category(
                categoryId = "0", title = "Android", icon = "any url"
            )
        )

        val actualArticleItems = testDb.articlesDao().findArticleItems()
        val testItems = TestObserver.test(actualArticleItems)

        testItems
            .assertHasValue()
            .assertValue { !it[0].isBookmark }

        testDb.articlePersonalInfosDao().toggleBookmarkOrInsert("0")

        testItems
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue { it[0].isBookmark }

        testDb.articlePersonalInfosDao().toggleBookmarkOrInsert("0")
        testItems
            .assertHasValue()
            .assertHistorySize(3)
            .assertValue { !it[0].isBookmark }
    }

    @Test
    fun test_tags_increment_use_count() {
        testDb.tagsDao().insert(listOf(Tag("#Android"), Tag("#iOS"), Tag("#Android")))
        val actualTags = testDb.tagsDao().findTags()
        val testItems = TestObserver.test(actualTags)
        testItems
            .assertHasValue()
            .assertValue(listOf("#Android", "#iOS"))


        testDb.tagsDao().incrementTagUseCount("#iOS")
        testItems
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue(listOf("#iOS", "#Android"))
    }


    @Test
    fun test_find_article_items_by_tag() {
        val expectedArticles = Array(3) {
            Article(
                id = "$it",
                title = "test article",
                description = "test description",
                categoryId = if (it == 1) "1" else "0",
                poster = "anyurl",
                date = Date(),
                updatedAt = Date(),
                author = Author(userId = "0", avatar = "any url", name = "John Doe")
            )
        }.toList()
        val expectedCounts = Array(3) {
            ArticleCounts(
                articleId = "$it",
                likes = if (it == 0) 0 else (1..200).random(),
                comments = (20..40).random(),
                readDuration = 10
            )
        }.toList()


        testDb.categoriesDao()
            .insert(Category(categoryId = "0", title = "Android", icon = "any url"))
        testDb.categoriesDao().insert(Category(categoryId = "1", title = "iOS", icon = "any url"))
        testDb.articlesDao().upsert(expectedArticles)
        testDb.articleCountsDao().upsert(expectedCounts)
        testDb.tagsDao().insert(listOf(Tag("#Android"), Tag("#iOS")))
        testDb.tagsDao().insertRefs(
            listOf(
                ArticleTagXRef(articleId = "0", tagId = "#Android"),
                ArticleTagXRef(articleId = "1", tagId = "#Android"),
                ArticleTagXRef(articleId = "0", tagId = "#iOS")
            )
        )

        val actualArticleItems = testDb.articlesDao().findArticlesByTagId("#Android")
        TestObserver.test(actualArticleItems)
            .assertHasValue()
            .assertValue { it.size == 2 }
    }

    @Test
    fun test_find_article_items_by_raw() {
        val expectedArticles = Array(3) {
            Article(
                id = "$it",
                title = if (it == 0) "first article" else "test article",
                description = "test description",
                categoryId = if (it == 1) "1" else "0",
                poster = "anyurl",
                date = Date(),
                updatedAt = Date(),
                author = Author(userId = "0", avatar = "any url", name = "John Doe")
            )
        }.toList()
        val expectedCounts = Array(3) {
            ArticleCounts(
                articleId = "$it",
                likes = if (it == 0) 0 else (1..200).random(),
                comments = (20..40).random(),
                readDuration = 10
            )
        }.toList()

        testDb.categoriesDao()
            .insert(Category(categoryId = "0", title = "Android", icon = "any url"))
        testDb.categoriesDao().insert(Category(categoryId = "1", title = "iOS", icon = "any url"))
        testDb.articlesDao().upsert(expectedArticles)
        testDb.articleCountsDao().upsert(expectedCounts)
        testDb.tagsDao().insert(listOf(Tag("#Android"), Tag("#iOS")))
        testDb.tagsDao().insertRefs(
            listOf(
                ArticleTagXRef(articleId = "0", tagId = "#Android"),
                ArticleTagXRef(articleId = "1", tagId = "#Android"),
                ArticleTagXRef(articleId = "0", tagId = "#iOS")
            )
        )
        testDb.articlePersonalInfosDao().toggleBookmarkOrInsert("0")
        var actualArticleItems =
            testDb.articlesDao().findArticlesByRaw(SimpleSQLiteQuery(ArticleFilter().toQuery()))
                .toTestList()
        assertEquals(3, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao()
            .findArticlesByRaw(SimpleSQLiteQuery(ArticleFilter(isBookmark = true).toQuery()))
            .toTestList()
        assertEquals(1, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao()
            .findArticlesByRaw(SimpleSQLiteQuery(ArticleFilter(categories = listOf("1")).toQuery()))
            .toTestList()
        assertEquals(1, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao()
            .findArticlesByRaw(SimpleSQLiteQuery(ArticleFilter(search = "fir").toQuery()))
            .toTestList()
        assertEquals(1, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao().findArticlesByRaw(
            SimpleSQLiteQuery(
                ArticleFilter(
                    isHashtag = true,
                    search = "#Android"
                ).toQuery()
            )
        )
            .toTestList()
        assertEquals(2, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao().findArticlesByRaw(
            SimpleSQLiteQuery(
                ArticleFilter(
                    isBookmark = true,
                    categories = listOf("0", "1")
                ).toQuery()
            )
        ).toTestList()
        assertEquals(1, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao().findArticlesByRaw(
            SimpleSQLiteQuery(
                ArticleFilter(
                    isBookmark = true,
                    search = "fir"
                ).toQuery()
            )
        ).toTestList()
        assertEquals(1, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao().findArticlesByRaw(
            SimpleSQLiteQuery(
                ArticleFilter(
                    isBookmark = true,
                    isHashtag = true,
                    search = "#iOS"
                ).toQuery()
            )
        ).toTestList()
        assertEquals(1, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao().findArticlesByRaw(
            SimpleSQLiteQuery(
                ArticleFilter(
                    search = "fir",
                    categories = listOf("0", "1")
                ).toQuery()
            )
        ).toTestList()
        assertEquals(1, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao().findArticlesByRaw(
            SimpleSQLiteQuery(
                ArticleFilter(
                    isBookmark = true,
                    search = "fir",
                    categories = listOf("0", "1")
                ).toQuery()
            )
        ).toTestList()
        assertEquals(1, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao().findArticlesByRaw(
            SimpleSQLiteQuery(
                ArticleFilter(
                    isHashtag = true,
                    search = "#Android",
                    categories = listOf("0", "1")
                ).toQuery()
            )
        ).toTestList()
        assertEquals(2, actualArticleItems.size)

        actualArticleItems = testDb.articlesDao().findArticlesByRaw(
            SimpleSQLiteQuery(
                ArticleFilter(
                    isBookmark = true,
                    isHashtag = true,
                    search = "#Android",
                    categories = listOf("0", "1")
                ).toQuery()
            )
        ).toTestList()
        assertEquals(1, actualArticleItems.size)
    }
}

private fun <Key, T> DataSource.Factory<Key, T>.toTestList(
    start: Int = 0,
    size: Int = 10
): List<T> {
    return (this.create() as LimitOffsetDataSource<T>).loadRange(start, size).toList()
}