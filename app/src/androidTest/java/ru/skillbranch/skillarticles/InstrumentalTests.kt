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
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.data.local.AppDb
import ru.skillbranch.skillarticles.data.local.MarkdownConverter
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.local.entities.*
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.repositories.ArticleFilter
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.extensions.data.toArticle
import ru.skillbranch.skillarticles.extensions.data.toArticleContent
import ru.skillbranch.skillarticles.extensions.data.toArticleCounts
import java.lang.Thread.sleep
import java.util.*


@RunWith(AndroidJUnit4::class)
class InstrumentalTest1 {
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
    fun module1() {
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


        val actualArticle = testDb.articlesDao().findArticleById(expectedArticle.id)
        val testItems = TestObserver.test(actualArticle)
        testItems
            .assertValue(null)

        testDb.articlesDao().insert(expectedArticle)

        testItems
            .assertHasValue()
            .assertValue(expectedArticle)

        testDb.articlesDao().update(expectedArticle.copy(title = "update title"))

        testItems
            .assertHasValue()
            .assertHistorySize(3)
            .assertValue(expectedArticle.copy(title = "update title"))

        testDb.articlesDao().delete(expectedArticle.copy(title = "update title"))

        testItems
            .assertHistorySize(4)
            .assertValue(null)
    }

    @Test
    fun module2() {
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

        val expectedPersonal = Array(4) {
            ArticlePersonalInfo(
                articleId = "${it.inc()}",
                isLike = true,
                isBookmark = false
            )
        }.toList()

        testDb.articlesDao().upsert(expectedArticles)
        testDb.articleCountsDao().upsert(expectedCounts)
        testDb.articlePersonalInfosDao().upsert(expectedPersonal)

        val actualArticles = testDb.articlesDao().findArticles()
        val actualArticleCounts = testDb.articleCountsDao().findArticleCounts()
        val actualPersonalInfos = testDb.articlePersonalInfosDao().findPersonalInfos()

        val testArticles = TestObserver.test(actualArticles)
        val testCounts = TestObserver.test(actualArticleCounts)
        val testPersonals = TestObserver.test(actualPersonalInfos)

        testArticles
            .assertHasValue()
            .assertValue { it.size == 3 }

        testCounts
            .assertHasValue()
            .assertValue { it.size == 3 }

        testDb.articlesDao().delete(expectedArticles[1])

        testArticles
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue { it.size == 2 }

        testCounts
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue { it.size == 2 }

        testPersonals
            .assertHasValue()
            .assertHistorySize(1)
            .assertValue { it.size == 4 }
    }

    @Test
    fun module3() {

        testDb.articlesDao().insert(
            Article(
                id = "0",
                title = "test article",
                description = "test description",
                categoryId = "0",
                poster = "anyurl",
                date = Date(),
                updatedAt = Date(),
                author = Author(userId = "0", avatar = "any url", name = "John Doe")
            )
        )
        val expectedCounts = ArticleCounts(
            articleId = "0",
            likes = 0,
            comments = 10,
            readDuration = 10
        )
        testDb.articleCountsDao().insert(expectedCounts)
        val expectedInfos = ArticlePersonalInfo(
            articleId = "0",
            isLike = true,
            isBookmark = false
        )

        TestObserver.test(testDb.articleCountsDao().findArticleCounts())
            .assertHasValue()
            .assertValue { it.size == 1 }

        TestObserver.test(testDb.articlePersonalInfosDao().findPersonalInfos())
            .assertHasValue()
            .assertValue { it.isEmpty() }

        val testCounts = TestObserver.test(testDb.articleCountsDao().findArticleCounts("0"))
        val testPersonal =
            TestObserver.test(testDb.articlePersonalInfosDao().findPersonalInfos("0"))

        testCounts
            .assertHasValue()
            .assertValue(expectedCounts)

        testDb.articleCountsDao().decrementLike("0")

        testCounts
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue { it.likes == 0 }

        testDb.articleCountsDao().incrementLike("0")
        testDb.articleCountsDao().incrementLike("0")

        testCounts
            .assertHasValue()
            .assertHistorySize(4)
            .assertValue { it.likes == 2 }

        testDb.articleCountsDao().decrementLike("0")

        testCounts
            .assertHasValue()
            .assertHistorySize(5)
            .assertValue { it.likes == 1 }

        testDb.articleCountsDao().incrementCommentsCount("0")

        testCounts
            .assertHasValue()
            .assertHistorySize(6)
            .assertValue { it.comments == 11 }

        TestObserver.test(testDb.articleCountsDao().getCommentsCount("0"))
            .assertHasValue()
            .assertValue(11)

        testPersonal
            .assertValue(null)

        testDb.articlePersonalInfosDao().toggleLikeOrInsert("0")

        testPersonal
            .assertHistorySize(2)
            .assertValue { it.isLike }

        testDb.articlePersonalInfosDao().toggleLikeOrInsert("0")

        testPersonal
            .assertHistorySize(3)
            .assertValue { !it.isLike }

        testDb.articlePersonalInfosDao().toggleBookmarkOrInsert("0")

        testPersonal
            .assertHistorySize(4)
            .assertValue { it.isBookmark }

        testDb.articlePersonalInfosDao().toggleBookmarkOrInsert("0")

        testPersonal
            .assertHistorySize(5)
            .assertValue { !it.isBookmark }
    }

    @Test
    fun module4() {
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
                likes = 0,
                comments = 20,
                readDuration = 10
            )
        }.toList()

        testDb.articlesDao().upsert(expectedArticles)
        testDb.articleCountsDao().upsert(expectedCounts)
        testDb.categoriesDao()
            .insert(Category(categoryId = "0", title = "Android", icon = "any url"))

        val actualArticleItems = testDb.articlesDao().findArticleItems()
        val testItems = TestObserver.test(actualArticleItems)

        testItems
            .assertHasValue()
            .assertValue { !it[0].isBookmark }
            .assertValue { it[0].likeCount == expectedCounts[0].likes }
            .assertValue { !it[1].isBookmark }
            .assertValue { it[2].commentCount == expectedCounts[0].comments }

        testDb.articlePersonalInfosDao().toggleBookmarkOrInsert("0")
        testDb.articlePersonalInfosDao().toggleLikeOrInsert("0")
        testDb.articlePersonalInfosDao().toggleLikeOrInsert("0")
        testDb.articlePersonalInfosDao().toggleBookmarkOrInsert("1")

        testDb.articleCountsDao().incrementCommentsCount("2")
        testDb.articleCountsDao().incrementLike("0")

        testItems
            .assertHasValue()
            .assertValue { it[0].isBookmark }
            .assertValue { it[0].likeCount == 1 }
            .assertValue { it[1].isBookmark }
            .assertValue { it[2].commentCount == 21 }
    }

    //raw query
    @Test
    fun module5() {
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

    //Articles repository tests
    @Test
    fun module6() {
        val repository = ArticlesRepository.apply {
            setupTestDao(
                articlesDao = testDb.articlesDao(),
                articleCountsDao = testDb.articleCountsDao(),
                categoriesDao = testDb.categoriesDao(),
                tagsDao = testDb.tagsDao(),
                articlePersonalDao = testDb.articlePersonalInfosDao()
            )
        }

        val expectedArticlesRes = NetworkDataHolder.findArticlesItem(0, 10)
        val expectedArticleItems = expectedArticlesRes.map {
            ArticleItem(
                id = it.data.id,
                date = it.data.date,
                author = it.data.author.name,
                authorAvatar = it.data.author.avatar,
                title = it.data.title,
                description = it.data.description,
                poster = it.data.poster,
                categoryId = it.data.category.categoryId,
                category = it.data.category.title,
                categoryIcon = it.data.category.icon,
                likeCount = it.counts.likes,
                commentCount = it.counts.comments,
                readDuration = it.counts.readDuration,
                isBookmark = false
            )
        }
        val expectedTags = listOf(
            "#Android",
            "#Android App Development",
            "#Kotlin",
            "#Architecture Components",
            "#Android Data Binding",
            "#Gradle",
            "#Fragments",
            "#Livedata",
            "#Viewmodel",
            "#Navigation Drawer",
            "#Programming",
            "#Database",
            "#Sqlite",
            "#iOS App Development",
            "#Sql",
            "#Swift",
            "#iOS",
            "#Mobile",
            "#Enumeration",
            "#App Development",
            "#Swift Programming",
            "#Development",
            "#Flutter",
            "Native App"
        )
        val expectedCategories = listOf(
            CategoryData(
                categoryId = "0",
                icon = "https://skill-branch.ru/img/mail/bot/android-icon.png",
                title = "Android",
                articlesCount = 6
            ),
            CategoryData(
                categoryId = "2",
                icon = "https://skill-branch.ru/img/mail/bot/ios-icon.png",
                title = "iOS",
                articlesCount = 2
            ),
            CategoryData(
                categoryId = "1",
                icon = "https://skill-branch.ru/img/mail/bot/db-icon.png",
                title = "Databases",
                articlesCount = 1
            ),
            CategoryData(
                categoryId = "3",
                icon = "https://skill-branch.ru/img/mail/bot/flutter-icon.png",
                title = "Flutter",
                articlesCount = 1
            )
        )

        val articlesRes = repository.loadArticlesFromNetwork(0, 10)
        assertEquals(expectedArticlesRes, articlesRes)

        repository.insertArticlesToDb(expectedArticlesRes)
        var actualArticleItems = repository.rawQueryArticles(ArticleFilter())
            .toTestList()

        assertEquals(expectedArticleItems, actualArticleItems)

        actualArticleItems = repository.rawQueryArticles(ArticleFilter(search = "draw"))
            .toTestList()
        assertEquals(listOf(expectedArticleItems[0]), actualArticleItems)

        actualArticleItems =
            repository.rawQueryArticles(ArticleFilter(search = "#Kotlin", isHashtag = true))
                .toTestList()
        assertEquals(
            listOf(
                expectedArticleItems[0],
                expectedArticleItems[1],
                expectedArticleItems[5]
            ), actualArticleItems
        )

        actualArticleItems = repository.rawQueryArticles(
            ArticleFilter(
                search = "#iOS",
                isHashtag = true,
                categories = listOf("2", "3")
            )
        )
            .toTestList()
        assertEquals(
            listOf(
                expectedArticleItems[7],
                expectedArticleItems[8],
                expectedArticleItems[9]
            ), actualArticleItems
        )

        repository.toggleBookmark("0")
        actualArticleItems = repository.rawQueryArticles(ArticleFilter(isBookmark = true))
            .toTestList()
        assertEquals(listOf(expectedArticleItems[0].copy(isBookmark = true)), actualArticleItems)

        val testTags = TestObserver.test(repository.findTags())
        testTags
            .assertHasValue()
            .assertValue(expectedTags)

        repository.incrementTagUseCount("#Kotlin")
        testTags
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue { it.first() == "#Kotlin" }

        TestObserver.test(repository.findCategoriesData())
            .assertHasValue()
            .assertValue(expectedCategories)
    }

    //Articles repository tests
    @Test
    fun module7() {
        val repository = ArticleRepository.apply {
            setupTestDao(
                articlesDao = testDb.articlesDao(),
                articleCountsDao = testDb.articleCountsDao(),
                articleContentDao = testDb.articleContentsDao(),
                articlePersonalDao = testDb.articlePersonalInfosDao()
            )
        }
        val expectedArticleRes = NetworkDataHolder.findArticlesItem(0, 1).first()
        val expectedArticleContent = NetworkDataHolder.loadArticleContent("0").toArticleContent()
        val expectedArticle = ArticleFull(
            id = expectedArticleRes.data.id,
            title = expectedArticleRes.data.title,
            description = expectedArticleRes.data.description,
            author = expectedArticleRes.data.author,
            category = expectedArticleRes.data.category,
            isBookmark = false,
            isLike = false,
            date = expectedArticleRes.data.date,
            content = null
        )


        testDb.articlesDao().insert(expectedArticleRes.data.toArticle())
        testDb.categoriesDao().insert(expectedArticleRes.data.category)
        testDb.articleCountsDao().insert(expectedArticleRes.counts.toArticleCounts())

        val testArticle = TestObserver.test(repository.findArticle("0"))
        testArticle
            .assertHasValue()
            .assertValue { it.title == expectedArticle.title }
            .assertValue { it.description == expectedArticle.description }
            .assertValue { it.category == expectedArticle.category }
            .assertValue { it.date == expectedArticle.date }
            .assertValue { it.author == expectedArticle.author }
            .assertValue { !it.isLike }
            .assertValue { !it.isBookmark }
            .assertValue { it.shareLink == null }
            .assertValue { it.content == null }

        repository.fetchArticleContent("0")

        testArticle
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue { it.title == expectedArticle.title }
            .assertValue { it.description == expectedArticle.description }
            .assertValue { it.category == expectedArticle.category }
            .assertValue { it.date == expectedArticle.date }
            .assertValue { it.author == expectedArticle.author }
            .assertValue { !it.isLike }
            .assertValue { !it.isBookmark }
            .assertValue { it.shareLink == expectedArticleContent.shareLink }
            .assertValue { it.content == MarkdownConverter().toMarkdown(expectedArticleContent.content) }


        val testPersonal =
            TestObserver.test(testDb.articlePersonalInfosDao().findPersonalInfos("0"))
        testPersonal
            .assertHasValue()
            .assertValue(null)

        repository.toggleBookmark("0")
        repository.toggleLike("0")

        testPersonal
            .assertHasValue()
            .assertHistorySize(3)
            .assertValue { it.articleId == "0" }
            .assertValue { it.isBookmark }
            .assertValue { it.isLike }

        testArticle
            .assertHasValue()
            .assertHistorySize(4)
            .assertValue { it.title == expectedArticle.title }
            .assertValue { it.description == expectedArticle.description }
            .assertValue { it.category == expectedArticle.category }
            .assertValue { it.date == expectedArticle.date }
            .assertValue { it.author == expectedArticle.author }
            .assertValue { it.isLike }
            .assertValue { it.isBookmark }
            .assertValue { it.shareLink == expectedArticleContent.shareLink }
            .assertValue { it.content == MarkdownConverter().toMarkdown(expectedArticleContent.content) }

        repository.toggleBookmark("0")
        repository.toggleLike("0")

        testPersonal
            .assertHasValue()
            .assertHistorySize(5)
            .assertValue { it.articleId == "0" }
            .assertValue { !it.isBookmark }
            .assertValue { !it.isLike }

        testArticle
            .assertHasValue()
            .assertHistorySize(6)
            .assertValue { it.title == expectedArticle.title }
            .assertValue { it.description == expectedArticle.description }
            .assertValue { it.category == expectedArticle.category }
            .assertValue { it.date == expectedArticle.date }
            .assertValue { it.author == expectedArticle.author }
            .assertValue { !it.isLike }
            .assertValue { !it.isBookmark }
            .assertValue { it.shareLink == expectedArticleContent.shareLink }
            .assertValue { it.content == MarkdownConverter().toMarkdown(expectedArticleContent.content) }

        val testCounts = TestObserver.test(testDb.articleCountsDao().findArticleCounts("0"))
        testCounts
            .assertHasValue()
            .assertValue { it.articleId == "0" }
            .assertValue { it.likes == expectedArticleRes.counts.likes }
            .assertValue { it.comments == expectedArticleRes.counts.comments }
            .assertValue { it.readDuration == expectedArticleRes.counts.readDuration }

        repository.incrementLike("0")
        repository.incrementLike("0")

        testCounts
            .assertHasValue()
            .assertHistorySize(3)
            .assertValue { it.likes == expectedArticleRes.counts.likes.plus(2) }

        repository.decrementLike("0")

        testCounts
            .assertHasValue()
            .assertHistorySize(4)
            .assertValue { it.likes == expectedArticleRes.counts.likes.plus(1) }

        val testCommentsCount = TestObserver.test(repository.findArticleCommentCount("0"))
        testCommentsCount
            .assertHasValue()
            .assertValue(expectedArticleRes.counts.comments)

        repository.sendMessage("0", "test", null)

        testCommentsCount
            .assertHasValue()
            .assertHistorySize(2)
            .assertValue(expectedArticleRes.counts.comments.inc())

        PrefManager.clearAll()

        val testSettings = TestObserver.test(repository.getAppSettings())
        testSettings
            .assertHasValue()
            .assertValue(AppSettings())

        repository.updateSettings(AppSettings(isDarkMode = true))
        repository.updateSettings(AppSettings())

        sleep(3000)

        testSettings
            .assertHasValue()
            .assertValueHistory(
                AppSettings(),
                AppSettings(isDarkMode = true),
                AppSettings()
            )


        val testAuth = TestObserver.test(repository.isAuth())
        testAuth
            .assertHasValue()
            .assertValueHistory(false)

        RootRepository.setAuth(true)

        sleep(3000)
        testAuth
            .assertHasValue()
            .assertValueHistory(false, true)
    }
}


private fun <Key, T> DataSource.Factory<Key, T>.toTestList(
    start: Int = 0,
    size: Int = 10
): List<T> {
    return (this.create() as LimitOffsetDataSource<T>).loadRange(start, size).toList()
}