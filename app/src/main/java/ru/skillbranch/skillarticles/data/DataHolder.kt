package ru.skillbranch.skillarticles.data

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.skillarticles.R
import java.util.*

object LocalDataHolder {
    private var isDalay = true
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val articleData = MutableLiveData<ArticleData?>(null)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val articleInfo = MutableLiveData<ArticlePersonalInfo?>(null)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val settings = MutableLiveData(AppSettings())

    fun findArticle(articleId: String): LiveData<ArticleData?> {
        GlobalScope.launch {
            if (isDalay) delay(2000)
            articleData.postValue(
                ArticleData(
                    title = "CoordinatorLayout Basic",
                    category = "Android",
                    categoryIcon = R.drawable.logo,
                    date = Date(),
                    author = "Skill-Branch"
                )
            )
        }
        return articleData

    }

    fun findArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        GlobalScope.launch {
            if (isDalay) delay(1000)
            articleInfo.postValue(ArticlePersonalInfo(isBookmark = true))
        }
        return articleInfo
    }

    fun getAppSettings() = settings
    fun updateAppSettings(appSettings: AppSettings) {
        settings.value = appSettings
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        articleInfo.value = info
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun disableDelay() {
        isDalay = false
    }
}

object NetworkDataHolder {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val content = MutableLiveData<List<Any>?>(null)
    private var isDelay = true

    fun loadArticleContent(articleId: String): LiveData<List<Any>?> {
        GlobalScope.launch {
            if (isDelay) delay(5000)
            content.postValue(listOf(longText))
        }
        return content
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun disableDelay() {
        isDelay = false
    }
}

data class ArticleData(
    val shareLink: String? = null,
    val title: String? = null,
    val category: String? = null,
    val categoryIcon: Any? = null,
    val date: Date,
    val author: Any? = null,
    val poster: String? = null,
    val content: List<Any> = emptyList()
)

data class ArticlePersonalInfo(
    val isLike: Boolean = false,
    val isBookmark: Boolean = false
)

data class AppSettings(
    val isDarkMode: Boolean = false,
    val isBigText: Boolean = false
)

val longText: String = """
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nibh sapien, consectetur et ultrices quis, convallis sit amet augue. Interdum et malesuada fames ac ante ipsum primis in faucibus. Vestibulum et convallis augue, eu hendrerit diam. Curabitur ut dolor at justo suscipit commodo. Curabitur consectetur, massa sed sodales sollicitudin, orci augue maximus lacus, ut elementum risus lorem nec tellus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Praesent accumsan tempor lorem, quis pulvinar justo. Vivamus euismod risus ac arcu pharetra fringilla.

Maecenas cursus vehicula erat, in eleifend diam blandit vitae. In hac habitasse platea dictumst. Duis egestas augue lectus, et vulputate diam iaculis id. Aenean vestibulum nibh vitae mi luctus tincidunt. Fusce iaculis molestie eros, ac efficitur odio cursus ac. In at orci eget eros dapibus pretium congue sed odio. Maecenas facilisis, dolor eget mollis gravida, nisi justo mattis odio, ac congue arcu risus sed turpis.

Sed tempor a nibh at maximus. Nam ultrices diam ac lorem auctor interdum. Aliquam rhoncus odio quis dui congue interdum non maximus odio. Phasellus ut orci commodo tellus faucibus efficitur. Nulla congue nunc vel faucibus varius. Sed cursus ut odio ut fermentum. Vivamus mattis vel velit et maximus. Proin in sapien pharetra, ornare metus nec, dictum mauris.

Praesent nisl nisl, iaculis id nulla in, congue eleifend leo. Sed aliquet elementum massa et gravida. Nulla facilisi. Cras convallis vestibulum elit et sodales. Cras consequat eleifend metus non tempus. Vivamus venenatis consequat mollis. Nunc suscipit ipsum at nunc dignissim porta. Sed accumsan tellus non mauris fermentum pulvinar. Morbi felis est, accumsan id est id, dictum interdum nulla. Proin ultrices, ante at placerat venenatis, tortor enim ullamcorper magna, at finibus nisi dui in ante. Vivamus convallis velit tortor, at mattis diam rhoncus vel. Integer at placerat turpis, vel laoreet nibh. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus fermentum tellus malesuada diam facilisis gravida. Quisque a semper ex, at semper dolor.

In a turpis suscipit, venenatis arcu id, condimentum nulla. Mauris id felis id metus aliquet facilisis ut sit amet lectus. In aliquam dapibus mollis. Morbi sollicitudin purus ultricies dictum feugiat. Morbi lobortis mollis faucibus. Nunc mattis nec est sagittis semper. Curabitur in dignissim elit.
""".trimIndent()
