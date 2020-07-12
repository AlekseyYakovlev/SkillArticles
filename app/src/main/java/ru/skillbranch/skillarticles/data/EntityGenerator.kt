package ru.skillbranch.skillarticles.data

import ru.skillbranch.skillarticles.data.models.ArticleData
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.data.models.CommentItemData
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.extensions.TimeUnits
import ru.skillbranch.skillarticles.extensions.add
import java.util.*
import kotlin.random.Random.Default.nextBoolean

object EntityGenerator {
    fun generateArticle(article: ArticleItemData): ArticleData = ArticleData(
        id = article.id,
        title = article.title,
        category = article.category,
        categoryIcon = article.categoryIcon,
        poster = article.poster,
        author = User(
            "${article.id.toInt() % 6}",
            article.author,
            article.authorAvatar,
            lastVisit = Date().add(-1 * (1..7).random(), TimeUnits.DAY),
            respect = (100..300).random(),
            rating = (100..200).random()
        ),
        commentCount = article.commentCount,
        likeCount = article.likeCount,
        readDuration = article.readDuration,
        date = article.date
    )

    fun generateArticleItems(count: Int): List<ArticleItemData> =
        Array(count) { articleItems[it % 6] }
            .toList()
            .mapIndexed { index, article ->
                article.copy(
                    id = "$index",
                    commentCount = (10..50).random(),
                    readDuration = (2..10).random(),
                    likeCount = (15..100).random(),
                    date = Date().add(-index, TimeUnits.DAY)
                )
            }

    fun generateComments(articleId: String, count: Int): List<CommentItemData> = Array(count) {
        CommentItemData(
            id = "$it",
            articleId = articleId,
            user = users[users.indices.random()],
            body = commentsContent[commentsContent.indices.random()],
            date = Date().add(-it, TimeUnits.DAY),
            slug = "$it/"
        )
    }
        .toList()
        .fold(mutableListOf()) { acc, comment ->
            val hasAnswer = nextBoolean()
            if (hasAnswer && acc.isNotEmpty()) {
                acc.add(
                    comment.copy(
                        answerTo = acc.last().user.name,
                        slug = "${acc.last().slug}${comment.slug}"
                    )
                )
            } else acc.add(comment)
            acc
        }

    fun createArticleItem(articleId: String): ArticleItemData {
        return articleItems[articleId.toInt() % 6].copy(id = articleId,
            commentCount = (10..50).random(),
            readDuration = (2..10).random(),
            likeCount = (15..100).random())
    }

}

private val articleItems = Array(6) {
    when (it) {
        1 -> ArticleItemData(
            id = "0",
            categoryIcon = "https://skill-branch.ru/img/mail/bot/android-category.png",
            category = "Android",
            title = "Architecture Components pitfalls",
            description = "LiveData and the Fragment lifecycle",
            author = "Christophe Beyls",
            authorAvatar = "https://miro.medium.com/fit/c/96/96/0*zhOjC9mtKiAzmBQo.png",
            poster = "https://miro.medium.com/max/800/1*Cd_1M-LJ46t6xo79LfMGVw.jpeg"
        )

        2 -> ArticleItemData(
            id = "0",
            categoryIcon = "https://skill-branch.ru/img/mail/bot/android-category.png",
            category = "Android",
            title = "Using Safe args plugin — current state of affairs",
            description = "Article describing usage of Safe args Gradle plugin with the Navigation Architecture Component and current support for argument types",
            author = "Veronika Petruskova",
            authorAvatar = "https://miro.medium.com/fit/c/96/96/1*VSq5CqY3y1Bb4CLK83ZIuw.png",
            poster = "https://miro.medium.com/max/1920/1*u4uWVOpqFCR1gGpJTewhhA.jpeg"
            )

        3 -> ArticleItemData(
            id = "0",
            categoryIcon = "https://skill-branch.ru/img/mail/bot/android-category.png",
            category = "Android",
            title = "Observe LiveData from ViewModel in Fragment",
            description = "Google introduced Android architecture components which are basically a collection of libraries that facilitate robust design, testable",
            author = "Sagar Begale",
            authorAvatar = "https://miro.medium.com/fit/c/96/96/2*0yEmon3hJKcxVIXjSJeR3Q.jpeg",
            poster = "https://miro.medium.com/max/1600/0*BDD1KysQZFMeH3pc.png"
            )

        4 -> ArticleItemData(
            id = "0",
            categoryIcon = "https://skill-branch.ru/img/mail/bot/android-category.png",
            category = "Android",
            title = "The New Android In-App Navigation",
            description = "How to integrate Navigation Architecture Component in your app in different use cases",
            author = "Veronika Petruskova",
            authorAvatar = "https://miro.medium.com/fit/c/96/96/1*VSq5CqY3y1Bb4CLK83ZIuw.png",
            poster = "https://miro.medium.com/max/6000/0*QocVcbGZ4MeJbTCZ"
        )

        5 -> ArticleItemData(
            id = "0",
            categoryIcon = "https://skill-branch.ru/img/mail/bot/android-category.png",
            category = "Android",
            title = "Optimizing Android ViewModel with Lifecycle 2.2.0",
            description = "Initialization, passing arguments, and saved state",
            author = "Adam Hurwitz",
            authorAvatar = "https://miro.medium.com/fit/c/96/96/2*0yEmon3hJKcxVIXjSJeR3Q.jpeg",
            poster = "https://miro.medium.com/max/4011/1*voHEHCw6ZWrWGMmZ_xtpBQ.png"
        )
        else -> ArticleItemData(
            id = "0",
            categoryIcon = "https://skill-branch.ru/img/mail/bot/android-category.png",
            category = "Android",
            author = "Florina Muntenescu",
            authorAvatar = "https://miro.medium.com/fit/c/96/96/1*z2H2HkOuv5bAOuIvUUN-5w.jpeg",
            title = "Drawing a rounded corner background on text",
            description = "Let’s say that we need to draw a **rounded** corner background on text, supporting the following cases",
            poster = "https://miro.medium.com/max/4209/1*GHjquSrfS6bNSjr_rsDSJw.png"
            )
    }
}.toList()

private val users = Array(5) {
    when (it) {
        1 -> User(
            id = "1",
            name = "Christophe Beyls",
            avatar = "https://miro.medium.com/fit/c/96/96/0*zhOjC9mtKiAzmBQo.png",
            rating = (0..100).random(),
            respect = (0..300).random(),
            lastVisit = Date().add(-1 * (1..7).random(), TimeUnits.DAY)
        )

        2 -> User(
            id = "2",
            name = "Veronika Petruskova",
            avatar = "https://miro.medium.com/fit/c/96/96/1*VSq5CqY3y1Bb4CLK83ZIuw.png",
            rating = (0..100).random(),
            respect = (0..300).random(),
            lastVisit = Date().add(-1 * (1..7).random(), TimeUnits.DAY)
        )

        3 -> User(
            id = "3",
            name = "Sagar Begale",
            avatar = "https://miro.medium.com/fit/c/96/96/2*0yEmon3hJKcxVIXjSJeR3Q.jpeg",
            rating = (0..100).random(),
            respect = (0..300).random(),
            lastVisit = Date().add(-1 * (1..7).random(), TimeUnits.DAY)
        )

        4 -> User(
            id = "4",
            name = "Florina Muntenescu",
            avatar = "https://miro.medium.com/fit/c/96/96/1*z2H2HkOuv5bAOuIvUUN-5w.jpeg",
            rating = (0..100).random(),
            respect = (0..300).random(),
            lastVisit = Date().add(-1 * (1..7).random(), TimeUnits.DAY)
        )

        else -> User(
            id = "0",
            name = "Adam Hurwitz",
            avatar = "https://miro.medium.com/fit/c/96/96/2*0yEmon3hJKcxVIXjSJeR3Q.jpeg",
            rating = (0..100).random(),
            respect = (0..300).random(),
            lastVisit = Date().add(-1 * (1..7).random(), TimeUnits.DAY)
        )
    }
}.toList()
private val commentsContent = listOf(
    "Nice.Thanks for sharing. Next you asked? We need to add math/scientific notations to TextView and also draw curvy (and straight, of course) microeconomics graphs e.g cost curve, demand and supply curve etc. It’d be nice if you can write about it for your next piece.",
    "Nice article,thanks for sharing",
    "Great article!",
    "Would be useful for one of my use cases. Would be nice to have that functionality.",
    "Thats what we need",
    "Much needed thanks a lot",
    "Sounds interesting, I will try it!",
    "Thanks for article, finally I understand how it works and for what it needed.",
    "Thank you for the explanation!\n\nthis seems like something that the compiler should optimize automatically."
)

val articleContent1: String = """
Let’s say that we need to draw a **rounded** corner background on text, supporting the following cases:

* Set the background on one line text
![Text on one line](https://miro.medium.com/max/1155/0*PWKx5cM1bjVjA1aW "Text on one line with a rounded corner background")
* Set the background on text over **two or multiple lines**
![Text on multiple lines](https://miro.medium.com/max/1155/0*sdYBIZugLh_DJFvu "Text on multiple lines with a rounded corner background")
* Set the background on **right-to-left text**
![](https://miro.medium.com/max/1155/0*sdYBIZugLh_DJFvu "Right to left text with rounded corner background")
How can we implement this? Read on to find out, or jump directly to the [sample code](https://github.com/googlesamples/android-text/tree/master/RoundedBackground-Kotlin).
### To span or not to span? This is the question!
In [previous articles](https://medium.com/google-developers/underspanding-spans-1b91008b97e4) we’ve covered styling sections of text (even [internationalized text](https://medium.com/google-developers/styling-internationalized-text-in-android-f99759fb7b8f)). The solution involved using either framework or custom spans. While spans are a great solution in many cases, they do have some limitations that make them unsuitable for our problem. Appearance spans, like `BackgroundColorSpan`, give us access to the `TextPaint`, allowing us to change elements like the background color of text, but are only able to draw a solid color and can’t control elements like the corner radius.
![](https://miro.medium.com/max/507/0*jPTOmlEs5d6MLtuV "Text using a BackgroundColorSpan")
We need to draw a drawable together with the text. We can implement a custom `ReplacementSpan` to draw the background and the text ourselves. However `ReplacementSpans` cannot flow into the next line, therefore we will not be able to support a multi-line background. They would rather look like [Chip](https://material.io/design/components/chips.html) , the Material Design component, where every element must fit on a single line.
Spans work at the `TextPaint` level, not the layout level. Therefore, they can’t know about the line number the text starts and ends on, or about the paragraph direction (left-to-right or right-to-left)
### Solution: custom TextView
Depending on the position of the text, we need to draw four different drawables as text backgrounds:

* Text fits on one line: we only need one drawable
* Text fits on 2 lines: we need drawables for the start and end of the text
* Text spans multiple lines: we need drawables for the start, middle and end of the text.
![](https://miro.medium.com/max/507/0*jPTOmlEs5d6MLtuV "The four drawables that need to be drawn depending on the position of the text")
To position the background, we need to:

* Determine whether the text spans multiple lines 
* Find the start and end lines 
* Find the start and end offset depending on the paragraph direction 

All of these can be computed based on the text [Layout](https://developer.android.com/reference/android/text/Layout). To render the background behind the text we need access to the `Canvas`. A custom `TextView` has access to all of the information necessary to position the drawables and render them.
Our solution involves splitting our problem into 4 parts and creating classes dealing with them individually:

* **Marking the position of the background** is done in the XML resources with `Annotation` spans and then, in the code, we compute the positions in the `SearchBgHelper`
* Providing the background **drawables** as **attributes** of the TextView — implemented in `TextRoundedBgAttributeReader`
* **Rendering the drawables** depending on whether the text runs across **one or multiple lines** — `SearchBgRenderer` interface and its implementations: `SingleLineRenderer` and `MultiLineRenderer`
* Supporting **custom drawing** on a `TextView` — `RoundedBgTextView`, a class that extends `AppCompatTextView`, reads the attributes with the help of `TextRoundedBgAttributeReader`, overrides onDraw where it uses `SearchBgHelper` to draw the background.
### Finding out where the background should be drawn
We specify parts of the text that should have a background by using `Annotation` spans in our string resources. Find out more about working with Annotation spans from [this article](https://medium.com/google-developers/styling-internationalized-text-in-android-f99759fb7b8f).

We created a `SearchBgHelper` class that:

* Enables us to position the background based on the text directionality: left-to-right or right-to-left
* Renders the background, based on the drawables and the horizontal and vertical padding

In the `SearchBgHelper.draw` method, for every `Annotation` span found in the text, we get the start and end index of the span, find the line number for each and then compute the start and end character offset (within the line). Then, we use the `SearchBgRenderer` implementations to render the background.
```fun draw(canvas: Canvas, text: Spanned, layout: Layout) {
    // ideally the calculations here should be cached since 
    // they are not cheap. However, proper
    // invalidation of the cache is required whenever 
    // anything related to text has changed.
    val spans = text.getSpans(0, text.length, Annotation::class.java)
    spans.forEach { span ->
        if (span.value.equals("rounded")) {
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)
            val startLine = layout.getLineForOffset(spanStart)
            val endLine = layout.getLineForOffset(spanEnd)

            // start can be on the left or on the right depending 
            // on the language direction.
            val startOffset = (layout.getPrimaryHorizontal(spanStart)
                + -1 * layout.getParagraphDirection(startLine) * horizontalPadding).toInt()
            // end can be on the left or on the right depending 
            // on the language direction.
            val endOffset = (layout.getPrimaryHorizontal(spanEnd)
                + layout.getParagraphDirection(endLine) * horizontalPadding).toInt()

            val renderer = if (startLine == endLine) singleLineRenderer else multiLineRenderer
            renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
        }
    }
}```
### Provide drawables as attributes
To easily supply drawables for different `TextViews` in our app, we define 4 custom attributes corresponding to the drawables and 2 attributes for the horizontal and vertical padding. We created a `TextRoundedBgAttributeReader` class that reads these attributes from the xml layout.
### Render the background drawable(s)
Once we have the drawables we need to draw them. For that, we need to know:

* The start and end line for the background
* The character offset where the background should start and end at.

We created an abstract class `SearchBgRenderer` that knows how to compute the top and the bottom offset of the line, but exposes an abstract `draw` function:
```abstract fun draw(canvas: Canvas,layout: Layout,startLine: Int,endLine: Int,startOffset: Int,endOffset: Int)```
The `draw` function will have different implementations depending on whether our text spans a single line or multiple lines. Both of the implementations work on the same principle: based on the line top and bottom, set the bounds of the drawable and render it on the canvas.

The single line implementation only needs to draw one drawable.
![Single line text with search rounded background](https://miro.medium.com/max/1155/0*HS6zOL8stqjTodpJ "Single line text")
The multi-line implementation needs to draw the start and the end of line drawables on the first, and last line respectively, then for each line in the middle, draw the middle line drawable.
![](https://miro.medium.com/max/1155/0*Tz3lRa59dixAI5LA "Multi-line text")
### Supporting custom drawing on a TextView
We extend `AppCompatTextView` and override `onDraw` to call SearchBgHelper.draw before letting the `TextView` draw the text.

**Caveat:** Our sample makes all the calculations for every `TextView.onDraw` method call. If you’re planning on integrating this implementation in your app, we strongly suggest to modify it and cache the calculations done in `SearchBgHelper`. Then, make sure you invalidate the cache whenever anything related to text, like text color, size or other properties, has changed.

***

The Android text APIs allow you a great deal of freedom to perform styling. Simple styling is possible with text attributes, styles, themes and spans. Complex custom styling is achievable by taking control of the `onDraw` method and making decisions on what and how to draw on the `Canvas`. Check out our sample for all implementation details.

What kind of text styling did you have to do that required custom `TextView` implementations? What kind of issues did you encounter? Tell us in the comments!
""".trimIndent()

val articleContent2: String = """
The new Android [Architecure Components](https://developer.android.com/topic/libraries/architecture/) are soon to be announced as stable after a few months of public testing.

A lot has already been written about the basics (starting with the very good documentation) so I won’t cover them here. Instead I would like to focus on important pitfalls that are mostly undocumented and rarely discussed and may cause issues in your applications if you miss them. In this first article, I’ll talk about our beloved Fragments.

> **Edit (14 may 2018):** Google has finally fixed the issue in support library 28.0.0 and AndroidX 1.0.0. See **solution 4** below.
> **Edit (13 march 2020):** `onActivityCreated()` has been officially deprecated and `onViewCreated()` should be used instead. The code samples in this article have been updated accordingly.

***

The Architecture Components provide default `ViewModelProvider` implementations for activities and fragments. They allow you to store `LiveData` instances inside a `ViewModel` to be reused across configuration changes. The usage with activities is quite straightforward because the activity lifecyle maps well to the `Lifecycle` interface of the Architecture Components, but **the fragment lifecycle is more complex** and may cause subtle side effects if you’re not being careful.
![](https://miro.medium.com/max/800/1*Cd_1M-LJ46t6xo79LfMGVw.jpeg "The Fragment lifecycle (simplified version)")
**Fragments can be detached and re-attached.** When they are detached, their view hierarchy is destroyed and they become invisible and inactive, but their instance is not destroyed. When they are later re-attached, a new view hierarchy is created and `onCreateView()` and `onViewCreated()` are called again.

For this reason, the usually recommended place to initialize `Loaders` and other asynchronous loading operations that will eventually interact with the view hierarchy is in `onViewCreated()`. We can assume this is also the best place to initialize `LiveData` instances by subscribing a new `Observer`. Most of the [official Architecture Components samples](https://github.com/googlesamples/android-architecture-components) also do it there. You would expect typical code to look like this:
```class Page1Fragment : Fragment() {

    private var textView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_page1, container, false)
        textView = view.findViewById(R.id.result)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vm = ViewModelProviders.of(this)
                .get(Page1ViewModel::class.java)

        vm.myData.observe(this, Observer { textView?.text = it })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textView = null
    }
}```
**There is a hidden problem with this code.** We subscribe an anonymous observer within the lifespan of the fragment (using the fragment as `LifecycleOwner`), which means it will automatically be unsubscribed when the fragment is destroyed. However, **when the fragment is detached and re-attached, it is not destroyed** and a new identical observer instance will be added in `onViewCreated()`, while the previous one has not been removed! The result is a growing number of identical observers being active at the same time and the same code being executed multiple times, which is both a memory leak and a performance problem (until the fragment is destroyed).

This problem concerns any fragment subscribing an observer for its own lifecycle in `onCreateView()` or later, without taking any other extra step to unsubscribe it.

Worse: it also impacts **retained fragments**, which are not destroyed during configuration changes but re-attached to a new Activity.

*How do we solve this?* There are a few solutions to explore, some better than others. Here are the ones I found so far.
### Observing in onCreate()
Your first attempt may be to simply move the observer subscription to `onCreate()` instead of `onViewCreated()`. But it won’t work as expected without adding more boilerplate code: `LiveData` keeps track of which result has been delivered to which observer, and **it won’t deliver the latest result again to a new view hierarchy because the observer hasn’t changed.** This means you have to manually check for a latest result and bind it to the new view hierarchy in `onViewCreated()` which is not very elegant:
```override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    vm = ViewModelProviders.of(this)
            .get(Page1ViewModel::class.java)

    vm.myData.observe(this, Observer(this::bindResult))
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    vm.myData.value?.apply(this::bindResult)
}

private fun bindResult(result: String?) {
    textView?.text = result
}```
Note that the binding code needs to be called at two different places, so you can’t just write an inline anonymous observer. And finally, there is no way with the current LiveData API to differentiate a null result from no result for the current value, so it’s better not to return any null result (for instance in case of error) when using this solution, which overall is probably the worst one.
### Manually unsubscribing the observer in onDestroyView()
This is a bit better than the first solution but you still can’t use an inline anonymous observer. You must declare it as a final field, subscribe it in `onViewCreated()` and also not forget to unsubscribe it in `onDestroyView()`, so there is still boilerplate code and room for errors.
```private val observer = Observer<String?> { textView?.text = it }

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    vm = ViewModelProviders.of(this)
            .get(Page1ViewModel::class.java)
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    vm.myData.observe(this, observer)
}

override fun onDestroyView() {
    super.onDestroyView()
    textView = null

    vm.myData.removeObserver(observer)
}```
One of the main benefits of using `LiveData` being that it takes care of unsubscribing the observer for you, it’s a pity that it has to be done manually in this case. Keep reading, we can still do better.
### Resetting an existing observer
It’s not actually required to unsubscribe the current observer precisely in `onDestroyView()`, because it will eventually be unsubscribed automatically in `onDestroy()`. What’s important is that it’s unsubscribed **before an identical one is subscribed** in `onViewCreated()`, in order to avoid duplicates. Therefore, another valid solution is to unsubscribe right before subscribing, using for example this Kotlin extension function:
```fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
    removeObserver(observer)
    observe(owner, observer)
}```
Removing and adding back the same observer will effectively reset its state so that `LiveData` will deliver the latest result again automatically during `onStart()`, if any. The above function can be used like this:
```private val observer = Observer<String?> { textView?.text = it }

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val vm = ViewModelProviders.of(this)
            .get(Page1ViewModel::class.java)

    vm.myData.reObserve(this, observer)
}```
The fragment code is now less error-prone because there is one less callback to add, but we still need to declare the observer instance as a final field and reuse it, or else unsubscription will silently fail. Despite this, it’s probably my personal favorite solution for a quick workaround.
### Creating a custom Lifecyle for view hierarchies
Ideally, we would want observers subscribed in `onViewCreated()` to be automatically unsubscribed in `onDestroyView()`. This means we actually want to follow the current *view hierarchy lifecycle,* which is different from the *fragment lifecycle*. One way to achieve this is to create a custom Fragment which provides an additional custom `LifecycleOwner` for the current view hierarchy. Here is an implementation in Java.
```package be.digitalia.archcomponentsfix.fragment;

import android.arch.lifecycle.Lifecycle.Event;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Fragment providing separate lifecycle owners for each created view hierarchy.
 * <p>
 * This is one possible way to solve issue https://github.com/googlesamples/android-architecture-components/issues/47
 *
 * @author Christophe Beyls
 */
public class ViewLifecycleFragment extends Fragment {

	static class ViewLifecycleOwner implements LifecycleOwner {
		private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

		@Override
		public LifecycleRegistry getLifecycle() {
			return lifecycleRegistry;
		}
	}

	@Nullable
	private ViewLifecycleOwner viewLifecycleOwner;

	/**
	 * @return the Lifecycle owner of the current view hierarchy,
	 * or null if there is no current view hierarchy.
	 */
	@Nullable
	public LifecycleOwner getViewLifecycleOwner() {
		return viewLifecycleOwner;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		viewLifecycleOwner = new ViewLifecycleOwner();
		viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Event.ON_CREATE);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (viewLifecycleOwner != null) {
			viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Event.ON_START);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (viewLifecycleOwner != null) {
			viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Event.ON_RESUME);
		}
	}

	@Override
	public void onPause() {
		if (viewLifecycleOwner != null) {
			viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Event.ON_PAUSE);
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		if (viewLifecycleOwner != null) {
			viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Event.ON_STOP);
		}
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		if (viewLifecycleOwner != null) {
			viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Event.ON_DESTROY);
			viewLifecycleOwner = null;
		}
		super.onDestroyView();
	}
}```
Thanks to the custom `LifecycleOwner` returned by `getViewLifecycleOwner()`, the LiveData observers will be automatically unsubscribed when the view hierarchy is destroyed and nothing else has to be done.

> Note: `onViewCreated()` won’t be called if `onCreateView()` returns `null`, so the custom `LifecycleOwner` won’t be created for headless fragments and `getViewLifecycleOwner()` will also return `null` in that case.

With this solution the code is now nearly identical to the initial code sample, but this time it does the right thing.
```override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val vm = ViewModelProviders.of(this)
            .get(Page1ViewModel::class.java)

    vm.myData.observe(viewLifecycleOwner,
            Observer { textView?.text = it })
}```
The downside is that it requires inheriting from a custom `Fragment` implementation. It is also possible to implement the same functionality without inheritance but then it requires declaring a delegate helper class inside each fragment where that custom `LifecycleOwner` is needed.

> **Edit (14 may 2018):** Google implemented this solution directly in support library 28.0.0 and AndroidX 1.0.0. All fragments now provide an additional `getViewLifecycleOwner()` method just like in the above sample, so you don’t need to implement it yourself.

### Using Data Binding
![](https://miro.medium.com/max/500/1*1IJHM1UI_SobnhwtS5ZB9g.jpeg "Data Binding")
> *This section has been rewritten following the release of Android Studio 3.1 and the described solution is considered production-ready.*

This last solution provides the cleanest architecture, at the expense of depending on an additional library. It consists of using the [Android Data Binding Library](https://developer.android.com/topic/libraries/data-binding/index.html) to automatically bind your model to the current view hierarchy, and to simplify things the model will be the `ViewModel` instance already used to contain the various `LiveData`.

With that architecture, the `LiveData` instances exposed by the `ViewModel` will be automatically observed by the generated `Binding` class instead of the fragment itself.
```<layout xmlns:android="http://schemas.android.com/apk/res/android">
    <data>
        <variable
            name="viewModel"
            type="my.app.viewmodel.Page1ViewModel"
            />
    </data>
    <TextView
        android:id="@+id/result"
        style="@style/TextAppearance.AppCompat.Large"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@{viewModel.myData}"/>
</layout>```
The layout declares a variable of type `Page1ViewModel` and binds the `TextView`’s `android:text` property directly to the `LiveData` field. When the `LiveData` is updated, the `TextView` will immediately reflect its new value.

> Note: The ability to bind `LiveData` fields directly to views and make the `Binding` classes lifecycle-aware has been added in **Android Studio 3.1.**

The `ViewModel` will always reflect the latest visual state of the fragment, even when no view hierarchy is currently attached to it. **Each time a view hierarchy is created through a Binding class, it will register itself as a new observer on the LiveData instances, so we don’t have to manage any observer manually anymore** and we got rid of the problem.
```class Page1Fragment : Fragment() {

    lateinit var vm: Page1ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProviders.of(this)
                .get(Page1ViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentPage1Binding
                .inflate(inflater, container, false)
        binding.viewModel = vm
        binding.setLifecycleOwner(this)
        return binding.root
    }
}```
Since the `LifecycleOwner` is passed to the `Binding` instance, the `LiveData` loading logic will still comply with the lifecycle of the current fragment: the views will only be updated while the fragment is started and the internal observers will be properly unsubscribed in `onDestroy()`.

Furthermore, the special `LiveData` observers used by the `Binding` classes use **weak references** internally so they will eventually be unregistered automatically after their associated view hierarchy has been destroyed and garbage collected, even if the fragment itself has not been destroyed yet.

Overall, this simplifies the fragment by removing the tricky observers logic along with all the `View` boilerplate code.

***

#### Final warning
This issue should be taken seriously because detaching a fragment is a very common operation. For instance, it happens when:

* Switching between sections in the main activity of an app;
* Navigating between pages in a ViewPager using a FragmentPagerAdapter;
* A fragment is replaced with another one and the transaction is added to the back stack.

Also, when a fragment is detached, all its child fragments are detached as well. When in doubt, it’s better to assume that any fragment will eventually be detached at some point and properly handle this case from day 1.

***

Now you are aware of this behavior and have at least a few options to work around it.

> **Edit (14 may 2018):** Google decided to implement solution 4 directly in support library 28.0.0 and AndroidX 1.0.0. I now recommend that you register your observers using the special lifecycle returned by `getViewLifecycleOwner()` in `onCreateView()`.
> I like to think that this article played a part in Google fixing this issue and picking up the proposed solution.

Don’t hesitate to discuss this in the comments section and please share if you like.
""".trimIndent()

val articleContent3: String = """
Couple of days ago I started working with the new Navigation Component that was presented at this years Google I/O and is part of Android Jetpack. While reading about it in official documentation, I stumbled upon a section about passing data between destinations using safe args Gradle plugin. As documentation states, safe args plugin

> generates simple object and builder classes for type-safe access to arguments specified for destinations and actions. Safe args is built on top of the Bundle.

So I thought I’ll give it a try and share my findings. I’ll show you how to pass data using this plugin and also what types of data is supported.

_**Note:** At the time of writing most recent release of safe args Gradle plugin was **alpha04**. I’ll try to update this post when things change in future._

***

### Type-safe data passing
Passing data using safe args plugin takes only a few steps. First you have to add a dependency to your project level gradle file:
```classpath "android.arch.navigation:navigation-safe-args-gradle-plugin:1.0.0-alpha04"```
and apply plugin in module level gradle file:
```apply plugin: "androidx.navigation.safeargs"```
Then you define the argument for target destination in your navigation graph (we assume you already defined at least two destinations in the graph).

This requires three parameters: argument name, type and default value. You can use right panel in design tab of navigation editor for that.

**Note:** When you select argument type it will generate this xml code: `app:type=”string”`. If you try to rebuild project, Android Studio will give you an **error:**

> The ‘type’ attribute used by argument ‘title’ is deprecated. Please change all instances of ‘type’ in navigation resources to ‘argType’.

It’s self explanatory enough so you know that you should change it to `app:argType=”string”` manually in xml. It’s because in plugin alpha04 release they’ve changed it. There’s already an issue on issue tracker for this so it should be fixed in upcoming releases.

When you’re done, switch to text tab and you should see something like this in your destination:
```<fragment ...>
<argument
    android:name="title"
    android:defaultValue="null"
    app:argType="string" />
</fragment>```
Rebuild project so that two necessary builder classes get generated. These java classes are then used to pass the value of the argument.

First class is used for inserting data. It has name after the direction from which you send data and ends with **‘Directions’** e.g. *HomeFragmentDirections*. Second one is used to retrieve data at target direction and its name ends with **‘Args’** e.g. *DetailFragmentArgs*.

If you look at the generated classes, you’ll see they’re using Bundle and properties matching the arguments defined in our xml. We can access these properties through getters and setters.

To send data from destination you need to obtain an instance of generated class which is named after **action** that you created for navigating to another destination (in this example it’s called *ActionDetail*). After that just insert your data using a setter method and pass *actionDetail* instance as a parameter in your *NavController.navigate* call.
```val actionDetail = HomeFragmentDirections.ActionDetail()
actionDetail.setTitle(item)
Navigation.findNavController(view).navigate(actionDetail)```
Receiving data in target destination depends on it being an activity or fragment. If you’re retrieving data in **activity** you would get the bundled data from extras in *onCreate* method:
```val username = MainActivityArgs.fromBundle(intent?.extras).username```
For retrieving data in **fragment** use arguments property in either *onCreate, onCreateView or onViewCreated* lifecycle methods:
```arguments?.let {
    val safeArgs = DetailFragmentArgs.fromBundle(it)
    val title = safeArgs.title
}```
And that’s all. You’ve successfully transferred your data using safe args.

***

### Supported data types
When you add arguments using design tab of navigation editor it lets you pick from only three types: **string, integer** and **reference**.

But we come from the Bundle world where we could pass all kinds of primitive types and also Parcelable objects. So I was curious and tried to change value of `app:argType` to **boolean** manually in xml.

It worked without problems. Then I found this issue on issue tracker where people asked for support for more types. So it looks like they’ve already done it for **boolean, long** and **float**. Using **char** and **double** as an argument type didn’t work though. Compile error was: `part ‘double’ is keyword.`

Then I tried passing a **reference**. Reference takes integer value so I thought it was for passing resource ids and my assumption was quickly confirmed by comment I found in the same [issue](https://issuetracker.google.com/issues/79563966):

> An argument with a type of “reference” is a reference to an Android resource i.e., with values like @string/value in XML or R.string.value in code.

There I’ve also learned that since safe args plugin alpha03 (I was using alpha04 at the time) it also supports **Parcelable**.

You should use `@null` as default value and you have to also add `app:nullable=”true”` to allow nullable types because arguments are considered non-null by default and Android Studio will give you an error.

Also on [page](https://developer.android.com/jetpack/docs/release-notes) with release notes was mentioned you should use a **fully qualified class** name for `app:argType`. So I tried following:
```<argument
 android:name=”item”
 android:defaultValue=”@null”
 app:nullable=”true”
 app:argType=”com.vepe.navigation.model.Item” />```
Where Item is a simple data class in my model layer implementing Parcelable interface. If you’re not familiar with `@Parcelize` annotation, look [here](https://proandroiddev.com/parcelable-in-kotlin-here-comes-parcelize-b998d5a5fcac#70a6).
```@Parcelize
data class Item(val index: Int, val title: String, val value: Double): Parcelable```
_**Note:** You have to use a full class name. Using `app:argType=”Item”` will result in compile error because generated classes won’t be able to find and import your class._

***

### Conclusion
It looks like safe args plugin currently supports more types than Android Studio 3.2 lets you know about. That’s understandable of course, since the studio is still in beta (beta5 at the time of writing). I guess they’ll update the Navigation editor UI to match this in future release.

But it’s nice to know that we’re already able to pass values of type long, float, boolean and also Parcelable objects and not just integer, string and reference.

***

_**Update:** Since the release of plugin version **1.0.0-alpha08** you can now pass also Serializable objects, enum values and arrays of primitive types and Parcelables._
```enum class Category {
    CLOTHES, SHOES, ACCESSORIES, BAGS, UNDEFINED
}```
_You can specify the default value to be used directly from your enum class:_
```<argument
    android:name="category"
    android:defaultValue="UNDEFINED"
    app:argType="com.vepe.navigation.model.Category"/>```
_For passing **arrays** of values define argument type for primitive types as follows:_
```app:argType="integer[]"```
_And similarly for arrays of Parcelables:_
```app:argType="com.vepe.navigation.model.Item[]"```

***

_**2nd update:** Most recent release of plugin version **1.0.0-alpha10** (and **1.0.0-alpha11**, which is a hotfix release for the previous one) bring Kotlin users nice ways to write less code while using safe args plugin._

_From now on Kotlin users can **lazily** get arguments using `by navArgs()` property delegate. For e.g. in the past I accessed username in my activity using fromBundle method._
```username = MainActivityArgs.fromBundle(intent?.extras).username```
_Using mentioned **property delegate** and specifying expected Args type you can now retrieve your arguments during variable declaration:_
```private val mainActivityArgs by navArgs<MainActivityArgs>()

// later in code access properties from args
username = mainActivityArgs.username```
_Other nice improvement is you can generate Kotlin code by applying plugin `'androidx.navigation.safeargs.kotlin'` instead of `'androidx.navigation.safeargs'` in your app or other module gradle file._

_There are some breaking changes related to this. Generated `Direction` and `NavDirections` classes (such as my ActionDetail class) no longer have a public constructor. Because of that you can no longer pass data through safe args the old way:_
```HomeFragmentDirections.ActionDetail().setItem(item)```
_You should only be interacting with the **generated static methods.** Name of method matches name of the action class. For the previous example this means using generated `actionDetail()` method which takes item as an argument:_
```HomeFragmentDirections.actionDetail(item)```
_For the complete list of new features and changes look [here](https://developer.android.com/jetpack/androidx/releases/navigation#1.0.0-alpha11). You can find up-to-date examples of using safe args in [my navigation repo](https://github.com/laVepe/navigation-component-example)._

***

That’s all for now. I hope you find information in this post useful. If you have any suggestions or questions, feel free to leave a comment below. 😉

Also this is my first post so every single clap will be really appreciated. 👏❤️
""".trimIndent()

val articleContent4: String = """
Google introduced Android architecture components which are basically a collection of libraries that facilitate robust design, testable, and maintainable apps. It includes convenient and less error-prone handling of LifeCycle and prevents memory leaks.

Although these components are easy to use with exhaustive documentation, using them inappropriately leads to several issues which could be difficult to debug.
### Problem
One such issue our team came across was observing LiveData from ViewModel in Fragment. Let's say we have two Fragments: FragmentA (which is currently loaded) & FragmentB which user can navigate to. FragmentA is observing data from ViewModel via LiveData.
#### When
* The user navigates to FragmentB, FragmentA gets replaced by FragmentB and the transaction is added to backstack.
* After some actions on FragmentB user presses the back button and returns to FragmentA

#### Then
* LiveData observer in FragmentA triggered twice for single emit.

Following is the code Snippet:
```@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final TestViewModel viewModel =          ViewModelProviders.of(getActivity()).get(TestViewModel.class);
    viewModel.getTestEntity().observe(this, testEntity -> {
         //Do something
    });
}```
If the user navigates to `FragmentB` again and presses back to visit `FragmetnA`, the LiveData observer was triggered thrice and it continued to increase
### Debugging Approach
The initial thought was somehow(due to Fragment going though lifecycle) `ViewModel` was triggering LiveData multiple data on the same Observer. We added the following log to ensure this is the case:
```@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final ProductListViewModel viewModel =
            ViewModelProviders.of(getActivity()).get(ProductListViewModel.class);
    viewModel.getProducts().observe(this, new Observer<List<ProductEntity>>() {
        @Override
        public void onChanged(List<ProductEntity> productEntities) {
            Log.d("TEST", "[onChanged]: " + hashCode());
            //Do something
        }
    });
}```
After closely observing the `hashCode()` we discovered that same LiveData was observed twice and whenever value for `LiveData` was set multiple `Observer` instances `onChanged()` were called. **This is because the observers were not getting removed when FragmentAwas getting replaced.**

One quick fix we did was to `removeObservers()` before observing again as follows:
```viewModel.getProducts().removeObservers(this);
viewModel.getProducts().observe(this, new Observer<List<ProductEntity>>() {
    @Override
    public void onChanged(List<ProductEntity> productEntities) {
        Log.d("TEST", "[onChanged]: " + hashCode());
        //Do something
    }
});```
Since its more of a workaround and would be difficult to maintain (each Observe requires `removeObservers`), I tried to find a proper fix.

In order to do that I had to understand:

1. Fragment Lifecycle
2. How LiveData observers are removed
3. Why `onActivityCreated` for observing LiveData?
### Fragment Lifecycle
After searching a bit I came across the following diagram which gave a better understanding of Fragment Lifecycle:

Further researching on `Fragment` I found there are two distinct lifecycles associated with fragment:

#### The lifecycle of the FragmetnFragment
![](https://miro.medium.com/max/1196/0*XOEAs_jDCwSargiV.png "Image from https://github.com/xxv/android-lifecycle")
Since the image itself is self-explanatory, I won’t go in details. More information can be found [here](https://guides.codepath.com/android/creating-and-using-fragments).
#### The lifecycle of each view hierarchy
This was something interesting which I never knew. The lifecycle of a Fragment’s view is:
![](https://miro.medium.com/max/838/1*g_clKmSJGSNKIoM6KdrCmw.png "Screenshot from dev doc")
### How LiveData observers are removed
Based on the [documentation](https://developer.android.com/topic/libraries/architecture/livedata#work_livedata):

> You can register an observer paired with an object that implements the `LifecycleOwner` interface. **This relationship allows the observer to be removed when the state of the corresponding `Lifecycle` object changes to `DESTROYED`.** This is especially useful for activities and fragments because they can safely observe `LiveData` objects and not worry about leaks—activities and fragments are instantly unsubscribed when their lifecycles are destroyed.

### Solution
* The lifecycle of `Fragment` when `FragmentA` is replaced by `FragmentB` and the transaction is added to backstack, the state of `FragmentA` lifecycle is `onDestroyView`.
* When the user presses back on `FragmentB` , `FragmentA` goes through `onCreateView()` → `onViewCreated` → `onActivityCreated`
* Since `FragmentA` is never destroyed, the previous `Observer` is never removed. As a result, each time `onActivityCreated` was called, a new `Observer` was registered with the previous one still around. This caused `onChanged()` called multiple times.
* One proper solution is to use `getViewLifeCycleOwner()` as LifeCycleOwer while observing `LiveData` inside `onActivityCreated` as follows:
```viewModel.getMainTab().observe(getViewLifecycleOwner(), new Observer<Integer>() {
    @Override
    public void onChanged(@Nullable Integer integer) {
        //Do something
    }
});```
#### Note:
> The first method where it is safe to access the view lifecycle is `onCreateView(LayoutInflater, ViewGroup, Bundle)` under the condition that you must return a non-null view **(an IllegalStateException will be thrown if you access the view lifecycle but don't return a non-null view).**
### But why not observe in onCreate instead of onActivityCreated?
Based on the [documentation](https://developer.android.com/topic/libraries/architecture/livedata#work_livedata):

> Generally, LiveData delivers updates only when data changes, and only to active observers. An exception to this behavior is that observers also receive an update when they change from an inactive to an active state. **Furthermore, if the observer changes from inactive to active a second time, it only receives an update if the value has changed since the last time it became active.**

If we observe in `onCreate` and Fragment's view is recreated (visible → backstack → comes back), we have to update the values from `ViewModel` manually. This is because `LiveData` will not call the observer since it had already delivered the last result to that observer.
""".trimIndent()

val articleContent5: String = """
During the past few weeks I had time to dive into the Navigation Architecture Component that Google presented at this years Google I/O. It is a part of Android [Jetpack](https://developer.android.com/jetpack/) and its main goal is to ease in-app navigation on Android.

I’m going to show you how navigation component can simplify your everyday Android stuff like navigating when clicking on adapter item or through navigation drawer.

I’ve also created a simple app in Kotlin where you’ll find everything mentioned in this article. So if you have trouble with something, you can look at the whole code at this repo.

***

### First things first
If you’re completely new to Navigation component, you can go through [this](https://proandroiddev.com/android-navigation-arch-component-a-curious-investigation-3e56e24126e1) post to learn how to create navigation graph with the use of Navigation editor. Or [this](https://medium.com/google-developer-experts/android-navigation-components-part-1-236b2a479d44) post series to also help structure your app better. And it’s always good practice to look at the [documentation.](https://developer.android.com/topic/libraries/architecture/navigation/navigation-implementing)

There’s also a [codelab](https://codelabs.developers.google.com/codelabs/android-navigation/#0) which is a great way to start if you’re not familiar with this component but you want to learn by coding.

Ready? OK, let’s start by taking a closer look at the Navigation Component.

***

### Navigation Component main players
Before diving into code we should have some knowledge about classes and interfaces that we’re going to work with. [Documentation](https://developer.android.com/reference/androidx/navigation/package-summary) mentions three main components:

* **NavGraph** — a collection of destinations. It can be inflated from layout file or created programmatically. You can have multiple navigation graphs in your application and they can be also nested.
* **NavHost** — an interface serving as a container that hosts the NavController.
* **NavController** — class managing navigation within NavHost by interacting with NavGraph.

Other classes you’ll use include:

**NavHostFragment** — implementation of NavHost for creating fragment destinations. It has its own NavController and navigation graph. Typically you would have one NavHostFragment per activity.

**Navigation** — helper class for obtaining NavController instance and for connecting navigation to UI events like a button click.

**NavigationUI** — class for connecting app navigation patterns like drawer, bottom navigation or actionbar with NavController.

***

### Let’s quickly refresh the basics
Let’s say you’ve created navigation graph XML file and you’ve created a fragment in your activity layout file.

Now you have to set its name to `NavHostFragment` to indicate that this will be the place for switching destinations while navigating inside this activity. And you have to associate it with an existing navigation graph XML file.

The result should look something like this:
```<fragment
    android:layout_width=”match_parent”
    android:layout_height=”match_parent”
    android:id=”@+id/nav_host”
    android:name=”androidx.navigation.fragment.NavHostFragment”
    app:navGraph=”@navigation/nav_graph”
    app:defaultNavHost=”true” //to intercept with system Back button
 />```
If you want to see it done programmatically, see the docs.

To navigate between destinations(activities or fragments) you can use:

* target **destination id** e.g. `navController.navigate(R.id.mainActivity)`
* **action id** e.g. `navController.navigate(R.id.actionDetail)`

If you don’t have `NavController` you can obtain it in multiple ways:

* in activity by getting NavHostFragment instance:
```val host: NavHostFragment = supportFragmentManager
   .findFragmentById(R.id.nav_host) as NavHostFragment? ?: return
val navController = host.navController```
* in **fragment** or **view** by calling `findNavController()`
* or by calling `Navigation.findNavController(view)` passing a **view** associated with `NavController`

To **go back** to previous destination in graph use `navController.navigateUp()` method.

To hook up view to navigation use onClickListener:
```button.setOnClickListener { view ->
  view.findNavController().navigate(R.id.actionDetail)
}```
or
```button.setOnClickListener(
 Navigation.createNavigateOnClickListener(R.id.actionDetail,  bundle))```
where first parameter is action id or destination id and second is **bundle** for passing data between destinations. There’s also one argument version of this function when you don’t need to send data.

***

### Returning data to destination
We know how to send data from first destination to second, but what about if we want to get some data back? When working with activities we would typically use `startActivityForResult` method and get the result in `onActivityResult` after second activity finishes. I searched if Navigation Component has something similar.

In [this](https://issuetracker.google.com/issues/79672220) issue someone from Google recommended using a **shared ViewModel.** For activities we should probably continue using the old way, because you can’t share ViewModel between activities like you can with fragments.

Also Google is shifting more towards single activity per app approach and if community follows, getting result back from activity will be less common.

But if you want to share data between **fragments**, simply create a **ViewModel scoped to activity** and use it in both fragments. Then you have access to properties in ViewModel from both fragments.

If you need to imitate sending data when navigating back, you can use **Event** which is a wrapper class for data sent via LiveData. Event was created especially for **one time consumable data** like displaying SnackBar or Toast message. You can learn more about that in an [article](https://medium.com/google-developers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150) from Jose Alcérreca.

***

### Simplifying drawer, ActionBar and bottom navigation
Working with Navigation component saves you not just the boilerplate when writing fragment transactions but it’s very useful also when initializing **navigation drawer.**

No need to setup `ActionBarDrawerToggle` and `NavigationItemSelectedListener` anymore. It turns this pile of code:
```val toggle = ActionBarDrawerToggle(this, drawer_layout, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
drawer_layout.addDrawerListener(toggle)
toggle.syncState()
toggle.isDrawerIndicatorEnabled = true
nav_view.setNavigationItemSelectedListener { menuItem ->
 when (menuItem.itemId) {
            R.id.nav_first -> go somewhere…
            R.id.nav_second -> go somewhere else…
 }
 menuItem.isChecked = true
 drawer_layout.closeDrawer(Gravity.START)
 true
}```
Into this:
```NavigationUI.setupWithNavController(nav_view, navController)
NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)```
Awesome, right?! 👍

For this to work, you need to match **item ids** in your drawer **menu** with **destination ids** in your navigation **graph.** So if you have fragment with id `@+id/home_fragment`, you need to use the same id for the corresponding menu item in drawer.

The second line in above code snippet sets hamburger menu icon in actionbar and connects it to drawer. Last thing to do is to override `onSupportNavigateUp` method so that click on the icon opens the drawer:
```override fun onSupportNavigateUp() = NavigationUI.navigateUp(drawer_layout, navController)```
If you want to setup only **actionbar** use the same method with only two arguments:
```setSupportActionBar(toolbar)
NavigationUI.setupActionBarWithNavController(this, navController)```
If you use **bottom navigation** instead, just call:
```NavigationUI.setupWithNavController(bottom_nav, navController)```

***

### Navigating from adapter item
Navigation component makes it easier to react to click events on items in `RecyclerView`. The old way would be to create a listener interface that the activity/fragment implements and pass its instance to adapter via constructor. Then pass it to `ViewHolder` so you can call the listener’s method inside item’s on click listener implementation.

With Navigation component you don’t need to create an interface nor pass a listener instance to adapter if you’re only interested in navigating to another screen. Just set `onClickListener` on item’s view as follows:
```view.setOnClickListener(
  Navigation.createNavigateOnClickListener(destination_or_action_id)
)```
If you want to also **send** some **data**, use two argument version of this method and pass data using safe args plugin (more about it [here](https://medium.com/@vepetruskova/using-safe-args-plugin-current-state-of-affairs-41b1f01e7de8)).
```val actionDetail = HomeFragmentDirections.ActionDetail()
actionDetail.setTitle(item)
view.setOnClickListener {          
  Navigation.createNavigateOnClickListener(R.id.actionDetail,   actionDetail.arguments)
}```
###  Conditional navigation
Maria Neumayer described this well in her [article](https://medium.com/a-problem-like-maria/a-problem-like-navigation-part-2-63e46a565d4b). To sum it up, this kind of navigation is useful when you have some **condition** that has to be met to navigate to target destination. Good example is a **login screen.**

Let’s say user wants to navigate from Home screen to Profile but hasn’t logged in yet. Instead of Profile, Login screen is shown. After a successful login user is taken to the Profile screen. If he navigates back, he should be taken straight to the Home screen. [Documentation](https://developer.android.com/topic/libraries/architecture/navigation/navigation-conditional) suggests that

> The Login destination should pop itself off the navigation stack after it returns to the Profile destination. Call the `popBackStack()` method when navigating back to the original destination.

In case your login flow consists from multiple screens, you can use:
```findNavController().popBackStack(R.id.premium_flow, true)```
Above example will pop all destinations in stack that came after the one specified in 1st parameter. Second parameter describes if the specified destination should be also popped from the stack.

If you’re implementing **login flow** using a **separate activity**, simply call `activity.finish()` before navigating from login to another activity.

This way the login activity won’t stay in the stack and navigating up won’t show it. This is useful also with `onboarding flow` at app launch, because you don’t want to let user return to it by pressing the back button.

***

### Common destinations and global actions
Common destination is a screen to which can user navigate from multiple parts of the app. For such cases we can use **global actions.** Unlike normal actions, they are defined on the same level as destinations — inside the root element of graph called `navigation`. They have an **id** and **target** destination. Global action can be used to navigate to its target destination from any other destination in the same navigation graph (nested graph also).

_**Note:** When you define a global action you also have to provide android:id to the navigation element._
```<navigation android:id="@+id/main" ...>
<action android:id="@+id/action_global"
    app:destination="@id/detailFragment"/>
<fragment
    android:id="@+id/detailFragment"
    .../>
</navigation>```

***

### Nested navigation graphs
As your app becomes more filled with features you may consider putting some destinations from your navigation graph to a **nested** graph. It’s basically a graph within your **main/root** graph. This is useful if you have a setup flow that consists from multiple screens or an onboarding flow.

[Creating](https://developer.android.com/topic/libraries/architecture/navigation/navigation-implementing#group) a nested graph is quite simple and can make your root graph look less complicated and easier to understand.

Also it enables you to **reuse** the nested flow in your app. That’s because a nested graph has an **id** which is used as the only access point for navigation from the root graph. You won’t be able to navigate to specific destination in nested graph from the root graph.
```<navigation android:id="@+id/main" ...>
<navigation android:id="@+id/premium_flow"
    app:startDestination="@id/premiumStep1Fragment">
    
    <fragment 
        android:id="@+id/premiumStep1Fragment" .../>
    <fragment
        android:id="@+id/premiumStep2Fragment" .../>
    </navigation>
</navigation>```

***

As you can see, Navigation Component has a lot to offer. It enables us to write less boilerplate code when implementing things like Navigation Drawer and simplifies fragment transactions. It surely is a nice contribution to the Architecture Components family.

There are still some topics that I haven’t covered in this article. **Deep linking** is well described in [this article](https://medium.com/google-developer-experts/android-navigation-components-part-3-19554ec9ae83). Navigation Component **testing** isn’t really covered in docs yet, but Maria Neumayer covers it briefly [here}(https://medium.com/a-problem-like-maria/a-problem-like-navigation-e9821625a70e).

If you have any suggestions or questions, feel free to leave a comment below. Don’t forget to leave 👏 if you liked the post. It will be appreciated. ❤️
""".trimIndent()

val articleContent6: String = """
The Lifecycle 2.2.0 update including simplified initialization with `by viewModels()` and `by activityViewModels()` syntax for the ViewModel (VM) component is great for quickly creating VMs. What about re-using the VM instance throughout the Fragment, passing arguments/parameters into the VM while also enabling saved state? With a bit of customization, the above can be achieved as I recently shipped in the latest version of the [Coinverse app](https://play.google.com/store/apps/details?id=app.coinverse).
### Setup
To take advantage of the latest VM component, declare the most recent [Lifecycle dependencies](https://developer.android.com/jetpack/androidx/releases/lifecycle#declaring_dependencies) in the *build.gradle (Module: app)* file for `lifecycle-viewmodel-ktx` and `lifecycle-livedata-ktx`. The `jvmTarget` also needs to be defined in order to implement the `by viewModels` syntax we’ll use below.
```android {
  ...
  kotlinOptions { jvmTarget = '1.8' }
}

dependencies {
  ...
  // ViewModel
  implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:X.X.X"
  // LiveData
  implementation "androidx.lifecycle:lifecycle-livedata-ktx:X.X.X"
}```
### Initializing the ViewModel for Reusability
![](https://miro.medium.com/max/4500/1*Po_iQ0JrM7EiVshbkp3Uug.jpeg "Photo by Stephanie Moody on Unsplash")
As the [ViewModel documentation](https://developer.android.com/topic/libraries/architecture/viewmodel?utm_campaign=android_series_viewmodeldoc_110817) outlines, a major advantage of utilizing VMs in an activity/fragment is the ability to save view state data that comprises the user interface. Otherwise, large portions of data would need to be reloaded every time there is a configuration change, or when the app temporarily goes into the background.

The documentation shows initiating the VM in `onCreate` as a method level `var`, and then later on as a class level `val`. Creating the VM in `onCreate` requires reassigning the variable on the class level in order to use the VM throughout the activity/fragment. For this reason, it’s preferred to [create the VM](https://developer.android.com/topic/libraries/architecture/viewmodel?utm_campaign=android_series_viewmodeldoc_110817#sharing)  as a class level `val`.

Rather than creating a `lateinit var` instance variable, declare a VM immutable `val`. The VMs values can only be accessed after the activity is attached to the application.
```class Fragment : Fragment() {
    private val viewModel: SomeViewModel by viewModels()

    private fun observeViewState() {
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            //viewState used here.
        }
    }
}```
### Differences between by viewModels and by activityViewModels
* Use `by viewModels` when the activity/fragment creating the VM will be the only activity/fragment accessing the VM’s data.

Behind the scenes `viewModels` is an extension function applied to either an activity/fragment that returns a VM with a lifecycle tied to that activity/fragment. A VM factory may optionally be defined which can be used to pass parameters/arguments as we’ll see below.

* Use by `activityViewModels` in fragments, when the fragment is sharing data/communicating with another activity/fragment.

This is useful for sharing information between views. `activityViewModels` is an extension function applied to a fragment that returns a fragment’s parent’s activity’s VM. A VM factory may be defined here as well.
#### Storing view state data
The VM is a great location to store cached view state data using an observable data structure such as [LiveData](https://developer.android.com/topic/libraries/architecture/livedata), [Coroutines](https://developer.android.com/topic/libraries/architecture/coroutines), or [RxKotlin](https://github.com/ReactiveX/RxKotlin).

* Use immutable public values to observe data.
* Use mutable private variables to edit values in the VM.
* Use a pattern that allows the readability of the values in the VM, such as LiveData’s transform or Coroutine’s collect.
#### VM lifecycle overview
* Created the first time an activity calls `onCreate`
* When an activity is recreated, it receives the same VM instance.
* When the creating activity/fragment is finished/detached, VM’s `onCleared` is called

### How to pass Arguments/Parameters to ViewModels
![](https://miro.medium.com/max/4224/1*4U9-bYTXxzuDqQXc93dnBQ.jpeg "Photo by Wilhelm Gunkel on Unsplash")
The advantage of passing data into the VM upon creation is that an important process may begin upon VM initialization, such as retrieving data to populate a view, rather than the process being tied to the fragment/activity lifecycle. This is important because as Lyla Fuijwara, a Google developer advocate explains, the VM instance is created once inside the activity/fragment. Then, the VM is stopped only when a user closes the view holding the VM, closes the app entirely, or if the Android system needs to clear the memory while the app is in the background.

If an initialization process is tied to an activity/fragment lifecycle event, it may unintentionally occur if the activity/fragment is recreated. When the process runs in the VM’s `init{..}` function, it will be tied to the VM lifecycle.
See: [ViewModels: Persistence](https://medium.com/androiddevelopers/viewmodels-persistence-onsaveinstancestate-restoring-ui-state-and-loaders-fc7cc4a6c090)
```// Override ViewModelProvider.NewInstanceFactory to create the ViewModel (VM).
class SomeViewModelFactory(private val someString: String): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = SomeViewModel(someString) as T
} 

class SomeViewModel(private val someString: String) : ViewModel() {
    init {
        //TODO: Use 'someString' to init process when VM is created. i.e. Get data request.
    }
}

class Fragment: Fragment() {
    // Create VM in activity/fragment with VM factory.
    val someViewModel: SomeViewModel by viewModels { SomeViewModelFactory("someString") } 
}```
#### Can’t we just use AndroidViewModel (AVM)?
AVM is an Android architecture component after all and allows one to pass parameters while providing application context. This seems tempting at first, however, it is problematic for unit tests. Unit tests should not deal with any of the Android lifecycle, such as context.
#### Data not to pass to the ViewModel
* Context, views, activities, fragments, adapters, Lifecycle, observe lifecycle-aware observables or hold resources, drawables etc. This information should be handled by the view and tethered to the view lifecycle.
* Don’t load resources, instead pass resource ids, and load in view. See: [AndroidViewModel antipattern](https://medium.com/androiddevelopers/locale-changes-and-the-androidviewmodel-antipattern-84eb677660d9)
### Enabling SavedState with Arguments/Parameters
![](https://miro.medium.com/max/3008/1*EB5rdW3aVNv_9NwpbJZ14Q.jpeg "Photo by Maurizio Pesce on flickr")
The [saved state module](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate) is a lifecycle component enabling saving data in the VM that will persist when the VM is stopped by the Android system. This may replace the functionality of saving data inside `onSaveInstanceState` (OSIS) using the activity/fragment bundle. Lyla also highlights the differences between `onSaveInstanceState` and `SavedState` (SS) in detail. The main difference with SS is that the data is stored within the VM instead of in the activity/fragment with OSIS.

OSIS and SS are meant to serve the same use case, saving small amounts of information required to restore the view state of the screen. The majority of data in the VM is meant for saving larger amounts of data that make up the view state. Therefore, SS may replace OSIS in order to organize both the smaller and larger view state data in one place, the VM.
See: [Save and restore UI state](https://medium.com/androiddevelopers/viewmodels-persistence-onsaveinstancestate-restoring-ui-state-and-loaders-fc7cc4a6c090#81a0)

With the Lifecycle 2.2.0 release, the saved state module comes standard with `by viewModels` or `by activityViewModels` syntax, without needing to create a `ViewModelProvider.NewInstanceFactory`. However, if setting default values for the saved state module or passing arguments/parameters to a VM, an `AbstractSavedStateViewModelFactory` factory is required, as the [documentation](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate#setup) notes. The factory class must handle the saved state owner, default values, and custom parameters.
#### DefaultArgs
The Bundle `defaultArgs` is recommended to pass into the ViewModelFactory, as the values will be used as defaults in the `SavedStateHandle` if there is not a value for the given key being requested by the saved state.
See: [Public constructors](https://developer.android.com/reference/androidx/lifecycle/AbstractSavedStateViewModelFactory#public-constructors_1) of the AbstractSavedStateViewModelFactory
#### Saved State
An `Int` is saved with the method `saveSomeInt`, in the `SavedStateHandle` to represent important information that needs to survive the VM being cleared. Then, `someInt` may be retrieved when needed. Because a default argument is defined for the key `SOME_INT_KEY`, the saved state will always have a value to work with for that key.
```class SomeViewModelFactory(
        private val owner: SavedStateRegistryOwner,
        private val defaultArgs: Bundle,
        private val someString: String) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, state: SavedStateHandle) =
            SomeViewModel(state, someString) as T
}

class SomeViewModel(private val state: SavedStateHandle, private val someString: String) : ViewModel() {
    // The default value is used from 'defaultArgs' since 'defaultArgs' are saved in the ViewModelFactory.
    val someInt = state.get<Int>(SOME_INT_KEY)
        
    init {
        //TODO: Use 'defaultString' and 'someString' to init process when VM is created. i.e. Get data request.
    }
        
     fun saveSomeInt(someInt: Int) {
        state.set(SOME_INT_KEY, position)
    }
}

class Fragment: Fragment() {
    // Create VM in activity/fragment with VM factory.
    val someViewModel: SomeViewModel by viewModels { SomeViewModelFactory(
            savedStateRegistryOwner = this, 
            defaultArgs = Bundle().apply {
               putInt(SOME_INT_KEY, 18)
            },
            someString = "someString") } 
    private var someInt: Int = 0
     
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        someViewModel.saveSomeInt(...)
    }    
        
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        someInt = someViewModel.someInt
    }
}```
""".trimIndent()

val articlesContent = listOf(
    articleContent1,
    articleContent2,
    articleContent3,
    articleContent4,
    articleContent5,
    articleContent6
)