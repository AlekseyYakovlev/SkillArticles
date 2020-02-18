package ru.skillbranch.skillarticles.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.skillarticles.R
import java.util.*

object LocalDataHolder {
    private val articleData = MutableLiveData<ArticleData?>(null)
    private val articleInfo = MutableLiveData<ArticlePersonalInfo?>(null)
    private val settings = MutableLiveData(AppSettings())

    fun findArticle(articleId: String): LiveData<ArticleData?> {
        GlobalScope.launch {
            delay(1000)
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
            delay(500)
            articleInfo.postValue(ArticlePersonalInfo(isBookmark = true))
        }
        return articleInfo
    }

    fun getAppSettings() = settings
    fun updateAppSettings(appSettings: AppSettings) {
        settings.value = appSettings
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        Log.e("DataHolder", "update personal info: $info");
        articleInfo.value = info
    }
}

object NetworkDataHolder {
    val content = MutableLiveData<String?>(null)
    fun loadArticleContent(articleId: String): LiveData<String?> {
        GlobalScope.launch {
            delay(500)
            content.postValue(longText)
        }
        return content
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
before header text
# Header1 first line margin middle line without margin last line with margin
## Header2 Header2
### Header3 Header3 Header3
#### Header4 Header4 Header4 Header4
##### Header5 Header5 Header5 Header5 Header5
###### Header6 Header6 Header6 Header6 Header6 Header6
after header text and break line

Emphasis, aka italics, with *asterisks* or _underscores_.

Strong emphasis, aka bold, with **asterisks** or __underscores__.

Strikethrough uses two tildes. ~~Scratch this.~~

Combined emphasis with **asterisks and _underscores_**.
or emphasis with __underscores and *asterisks*__.
or _underscores for italic and **asterisks for inner bold**_.
or *asterisks for italic and __underscores for inner bold__*.
or strikethrough ~~two tildes for strike~~

And combine with asterisks and underscores ~~two tildes for strike and __underscores for inner strike bold__ and **asterisks for inner strike bold**~~.
and combined emphasis together ~~two tildes for strike and __underscores for inner *strike italic bold*__ and **asterisks for inner _strike italic bold_**~~.

* Unordered list can use double **asterisks** or double __underscores__ for emphasis aka **bold**
- Use minuses for list item and _underscores_ and *asterisks* for emphasis aka *italic*
+ Or use plus for list item and ~~double tildes~~ for strike

1. First ordered list item 
2. Second item 
3. Third item.

> Blockquotes are very handy in ~~email~~ to emulate reply text.
> This line is *part* of __the__ same quote.

Use ` for wrap `inline code` split `code with line break
not` work `only inline`

simple single line 

Use ``` for wrap block code
```code block.code block.code block```
also it work for multiline code block 
```multiline code block
multiline code block
multiline code block
multiline code block```
Use three underscore character _ in new line for horizontal divider
___
or three asterisks
***
or three minus
---

simple text and break line

For inline link use `[for title]` and `(for link)` 
example link: [I'm an inline-style link](https://www.google.com)
simple text and break line

end markdown text
""".trimIndent()
