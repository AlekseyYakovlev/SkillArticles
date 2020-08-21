package ru.skillbranch.skillarticles

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import ru.skillbranch.skillarticles.TestStubs.stubArticle
import ru.skillbranch.skillarticles.TestStubs.stubArticleCounts
import ru.skillbranch.skillarticles.data.local.AppDb
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.data.remote.err.ApiError
import ru.skillbranch.skillarticles.data.remote.err.NoNetworkError
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.data.repositories.RootRepository


@RunWith(AndroidJUnit4::class)
class RepositoryTest1 {
    private lateinit var testDb: AppDb
    private lateinit var server: MockWebServer
    private lateinit var articlesRepository: ArticlesRepository
    private lateinit var articleRepository: ArticleRepository

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            AppConfig.BASE_URL = "http://localhost:8080/"

        }
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start(8080)

        testDb = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDb::class.java
        ).build()

        testDb.clearAllTables()

        articlesRepository = ArticlesRepository.apply {
            setupTestDao(
                articlesDao = testDb.articlesDao(),
                articleCountsDao = testDb.articleCountsDao(),
                categoriesDao = testDb.categoriesDao(),
                tagsDao = testDb.tagsDao(),
                articlePersonalDao = testDb.articlePersonalInfosDao(),
                articlesContentDao = testDb.articleContentsDao()
            )
        }

        articleRepository = ArticleRepository.apply {
            setupTestDao(
                articlesDao = testDb.articlesDao(),
                articleCountsDao = testDb.articleCountsDao(),
                articlePersonalDao = testDb.articlePersonalInfosDao(),
                articleContentDao = testDb.articleContentsDao()
            )
        }
    }

    @After
    fun tearDown() {
        testDb.close()
        server.shutdown()
    }


    @Test
    fun last_article_id() {
        runBlocking {
            val last = articlesRepository.findLastArticleId()
            Assert.assertEquals(null, last)
        }

        runBlocking {
            testDb.articlesDao().insert(stubArticle)
        }

        runBlocking {
            val last = articlesRepository.findLastArticleId()
            Assert.assertEquals("5f27d6cf83218a001d059af0", last)
        }
    }


    @Test
    fun load_initial() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(TestStubs.articlesInitialRes)
        )

        runBlocking {
            val articles = articlesRepository.loadArticlesFromNetwork(null, 1)
            val actual = testDb.articlesDao().findArticlesTest()
            val recordedRequest = server.takeRequest();

            Assert.assertEquals(1, articles)
            Assert.assertEquals(1, actual.size)
            Assert.assertEquals("5f27d6cf83218a001d059af0", actual.firstOrNull()?.id)
            Assert.assertEquals("GET", recordedRequest.method)
            Assert.assertEquals("/articles?limit=1", recordedRequest.path)
        }
    }


    @Test
    fun load_after() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(TestStubs.articlesAfterRes)
        )

        runBlocking {
            val articles =
                articlesRepository.loadArticlesFromNetwork("5f27d6cf83218a001d059af0", 2)
            val actual = testDb.articlesDao().findArticlesTest()
            val recordedRequest = server.takeRequest();

            Assert.assertEquals(2, articles)
            Assert.assertEquals(2, actual.size)
            Assert.assertEquals("5f27d6cf83218a001d059aea", actual.lastOrNull()?.id)
            Assert.assertEquals(
                "/articles?last=5f27d6cf83218a001d059af0&limit=2",
                recordedRequest.path
            )
        }
    }


    @Test
    fun load_before_empty() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        runBlocking {
            val articles =
                articlesRepository.loadArticlesFromNetwork("5f27d6cf83218a001d059af0", -2)
            val actual = testDb.articlesDao().findArticlesTest()
            val recordedRequest = server.takeRequest()

            Assert.assertEquals(0, articles)
            Assert.assertEquals(0, actual.size)
            Assert.assertEquals(null, actual.lastOrNull())
            Assert.assertEquals(
                "/articles?last=5f27d6cf83218a001d059af0&limit=-2",
                recordedRequest.path
            )
        }
    }

    @Test
    fun load_before() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(TestStubs.articlesBeforeRes)
        )

        runBlocking {
            val articles =
                articlesRepository.loadArticlesFromNetwork("5f27d6cf83218a001d059aea", -4)
            val actual = testDb.articlesDao().findArticlesTest()
            val recordedRequest = server.takeRequest()

            Assert.assertEquals(2, articles)
            Assert.assertEquals(2, actual.size)
            Assert.assertEquals("5f27d6cf83218a001d059aed", actual.lastOrNull()?.id)
            Assert.assertEquals(
                "/articles?last=5f27d6cf83218a001d059aea&limit=-4",
                recordedRequest.path
            )
        }
    }

    @Test
    fun toggle_bookmark() {
        runBlocking {
            testDb.articlesDao().insert(stubArticle)
        }

        runBlocking {
            var isBookmarked = articlesRepository.toggleBookmark("5f27d6cf83218a001d059af0")
            Assert.assertEquals(true, isBookmarked)

            var personal =
                testDb.articlePersonalInfosDao().findPersonalInfosTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(true, personal.isBookmark)

            isBookmarked = articlesRepository.toggleBookmark("5f27d6cf83218a001d059af0")
            Assert.assertEquals(false, isBookmarked)

            personal =
                testDb.articlePersonalInfosDao().findPersonalInfosTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(false, personal.isBookmark)
        }
    }

    @Test
    fun fetch_remove_content() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(TestStubs.articleContentRes)
        )
        runBlocking {
            testDb.articlesDao().insert(stubArticle)
        }

        runBlocking {
            NetworkMonitor.setNetworkIsConnected(false)
            try {
                articlesRepository.fetchArticleContent("5f27d6cf83218a001d059af0")
            } catch (e: Throwable) {
                Assert.assertEquals(true, e is NoNetworkError)
            }
            NetworkMonitor.setNetworkIsConnected(true)

            articlesRepository.fetchArticleContent("5f27d6cf83218a001d059af0")
            var actual = testDb.articleContentsDao().findArticlesContentsTest()
            Assert.assertEquals(1, actual.size)
            Assert.assertEquals("5f27d6cf83218a001d059af0", actual.firstOrNull()?.articleId)

            articlesRepository.removeArticleContent("5f27d6cf83218a001d059af0")
            actual = testDb.articleContentsDao().findArticlesContentsTest()
            Assert.assertEquals(0, actual.size)
        }
    }

    @Test
    fun login() {

        val repository = RootRepository
        runBlocking {
            try {
                server.enqueue(
                    MockResponse()
                        .setResponseCode(400)
                        .setBody("""{"message": "Wrong login or password"}""")
                )
                repository.login("test", "test")
            } catch (e: Throwable) {
                Log.d("123456", e.toString())
                Assert.assertEquals(true, e is ApiError.BadRequest)
                Assert.assertEquals("Wrong login or password", e.message)
            }

            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(TestStubs.loginRes)
            )
            repository.login("makweb@yandex.ru", "test123456789")
            Assert.assertEquals("Bearer test_acess_token", PrefManager.accessToken)
            Assert.assertNotNull(PrefManager.profile)
            Assert.assertEquals("5f27dad7966af6001c228d3b", PrefManager.profile?.id)

            val recordedRequest = server.takeRequest()
            Assert.assertEquals("POST", recordedRequest.method)
            Assert.assertEquals("/auth/login", recordedRequest.path)
            Assert.assertEquals(
                "[text={\"login\":\"test\",\"password\":\"test\"}]",
                recordedRequest.body.toString()
            )
        }
    }

    @Test
    fun toggle_like() {
        runBlocking {
            testDb.articlesDao().insert(stubArticle)
            testDb.articleCountsDao().insert(stubArticleCounts)
        }

        runBlocking {
            var isLiked = articleRepository.toggleLike("5f27d6cf83218a001d059af0")
            Assert.assertEquals(true, isLiked)

            var personal =
                testDb.articlePersonalInfosDao().findPersonalInfosTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(true, personal.isLike)

            isLiked = articleRepository.toggleLike("5f27d6cf83218a001d059af0")
            Assert.assertEquals(false, isLiked)

            personal =
                testDb.articlePersonalInfosDao().findPersonalInfosTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(false, personal.isLike)
        }
    }

    @Test
    fun increment_like() {
        runBlocking {
            testDb.articlesDao().insert(stubArticle)
            testDb.articleCountsDao().insert(stubArticleCounts)
        }

        runBlocking {
            NetworkMonitor.setNetworkIsConnected(false)
            articleRepository.incrementLike("5f27d6cf83218a001d059af0")
            var counts =
                testDb.articleCountsDao().findArticlesCountsTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(1, counts.likes)

            NetworkMonitor.setNetworkIsConnected(true)
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("""{"message": "Article marked as liked","likeCount": 5}""")
            )
            articleRepository.incrementLike("5f27d6cf83218a001d059af0")
            counts = testDb.articleCountsDao().findArticlesCountsTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(5, counts.likes)

            val recordedRequest = server.takeRequest()
            Assert.assertEquals("POST", recordedRequest.method)
            Assert.assertEquals(
                "/articles/5f27d6cf83218a001d059af0/incrementLikes",
                recordedRequest.path
            )
            Assert.assertEquals("Bearer test_acess_token", recordedRequest.headers["Authorization"])

            server.enqueue(
                MockResponse()
                    .setResponseCode(400)
                    .setBody("""{"message": "Article already mark as liked"}""")
            )
            try {
                articleRepository.incrementLike("5f27d6cf83218a001d059af0")
            } catch (e: Throwable) {
                Assert.assertEquals(true, e is ApiError.BadRequest)
                Assert.assertEquals("Article already mark as liked", e.message)
            }

            counts = testDb.articleCountsDao().findArticlesCountsTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(5, counts.likes)
        }
    }

    @Test
    fun decrement_like() {
        runBlocking {
            testDb.articlesDao().insert(stubArticle)
            testDb.articleCountsDao().insert(stubArticleCounts)
        }

        runBlocking {
            NetworkMonitor.setNetworkIsConnected(false)
            articleRepository.decrementLike("5f27d6cf83218a001d059af0")
            var counts =
                testDb.articleCountsDao().findArticlesCountsTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(0, counts.likes)

            NetworkMonitor.setNetworkIsConnected(true)
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("""{"message": "Don`t like it anymore","likeCount": 5}""")
            )
            articleRepository.decrementLike("5f27d6cf83218a001d059af0")
            counts = testDb.articleCountsDao().findArticlesCountsTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(5, counts.likes)

            val recordedRequest = server.takeRequest()
            Assert.assertEquals("POST", recordedRequest.method)
            Assert.assertEquals(
                "/articles/5f27d6cf83218a001d059af0/decrementLikes",
                recordedRequest.path
            )
//            Assert.assertEquals("Bearer test_acess_token", recordedRequest.headers["Authorization"])

            server.enqueue(
                MockResponse()
                    .setResponseCode(400)
                    .setBody("""{"message": "Article already don`t like"}""")
            )
            try {
                articleRepository.decrementLike("5f27d6cf83218a001d059af0")
            } catch (e: Throwable) {
                Assert.assertEquals(true, e is ApiError.BadRequest)
                Assert.assertEquals("Article already don`t like", e.message)
            }

            counts = testDb.articleCountsDao().findArticlesCountsTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(5, counts.likes)
        }
    }

    @Test
    fun add_bookmark() {
        runBlocking {
            testDb.articlesDao().insert(stubArticle)
        }

        runBlocking {
            NetworkMonitor.setNetworkIsConnected(false)
            articleRepository.addBookmark("5f27d6cf83218a001d059af0")

            NetworkMonitor.setNetworkIsConnected(true)
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("""{"message": "Add to bookmarks"}""")
            )
            articleRepository.addBookmark("5f27d6cf83218a001d059af0")

            val recordedRequest = server.takeRequest()
            Assert.assertEquals("POST", recordedRequest.method)
            Assert.assertEquals(
                "/articles/5f27d6cf83218a001d059af0/addBookmark",
                recordedRequest.path
            )
//            Assert.assertEquals("Bearer test_acess_token", recordedRequest.headers["Authorization"])

            server.enqueue(
                MockResponse()
                    .setResponseCode(400)
                    .setBody("""{"message": "Article already don`t like"}""")
            )
            try {
                articleRepository.addBookmark("5f27d6cf83218a001d059af0")
            } catch (e: Throwable) {
                Assert.assertEquals(true, e is ApiError.BadRequest)
                Assert.assertEquals("Article already don`t like", e.message)
            }

        }
    }

    @Test
    fun remove_bookmark() {
        runBlocking {
            testDb.articlesDao().insert(stubArticle)
        }

        runBlocking {
            NetworkMonitor.setNetworkIsConnected(false)
            articleRepository.removeBookmark("5f27d6cf83218a001d059af0")

            NetworkMonitor.setNetworkIsConnected(true)
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("""{"message": "Remove from bookmarks"}""")
            )
            articleRepository.removeBookmark("5f27d6cf83218a001d059af0")

            val recordedRequest = server.takeRequest()
            Assert.assertEquals("POST", recordedRequest.method)
            Assert.assertEquals(
                "/articles/5f27d6cf83218a001d059af0/removeBookmark",
                recordedRequest.path
            )
//            Assert.assertEquals("Bearer test_acess_token", recordedRequest.headers["Authorization"])

            server.enqueue(
                MockResponse()
                    .setResponseCode(400)
                    .setBody("""{"message": "Already remove from bookmarks"}""")
            )
            try {
                articleRepository.removeBookmark("5f27d6cf83218a001d059af0")
            } catch (e: Throwable) {
                Assert.assertEquals(true, e is ApiError.BadRequest)
                Assert.assertEquals("Already remove from bookmarks", e.message)
            }

        }
    }

    @Test
    fun refresh_comment_count() {
        runBlocking {
            testDb.articlesDao().insert(stubArticle)
            testDb.articleCountsDao().insert(stubArticleCounts)
        }

        runBlocking {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(TestStubs.articleCountsRes)
            )
            articleRepository.refreshCommentsCount("5f27d6cf83218a001d059af0")
            val recordedRequest = server.takeRequest()
            Assert.assertEquals("GET", recordedRequest.method)
            Assert.assertEquals("/articles/5f27d6cf83218a001d059af0/counts", recordedRequest.path)

            val counts =
                testDb.articleCountsDao().findArticlesCountsTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(6, counts.comments)
        }
    }

    @Test
    fun send_message() {
        runBlocking {
            testDb.articlesDao().insert(stubArticle)
            testDb.articleCountsDao().insert(stubArticleCounts)
        }

        runBlocking {

            var counts =
                testDb.articleCountsDao().findArticlesCountsTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(6, counts.comments)

            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(TestStubs.messageRes)
            )
            articleRepository.sendMessage(
                articleId = "5f27d6cf83218a001d059af0",
                message = "test",
                answerToMessageId = "5f37fd01bd6351001c26a71e"
            )
            counts = testDb.articleCountsDao().findArticlesCountsTest("5f27d6cf83218a001d059af0")
            Assert.assertEquals(9, counts.comments)

            val recordedRequest = server.takeRequest()
            Assert.assertEquals("POST", recordedRequest.method)
            Assert.assertEquals("/articles/5f27d6cf83218a001d059af0/messages", recordedRequest.path)
//            Assert.assertEquals("Bearer test_acess_token", recordedRequest.headers["Authorization"])
            Assert.assertEquals(
                "[text={\"message\":\"test\",\"answerTo\":\"5f37fd01bd6351001c26a71e\"}]",
                recordedRequest.body.toString()
            )

        }
    }
}